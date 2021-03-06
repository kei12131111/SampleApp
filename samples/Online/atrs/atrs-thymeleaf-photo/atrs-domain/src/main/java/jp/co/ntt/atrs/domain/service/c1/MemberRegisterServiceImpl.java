/*
 * Copyright(c) 2015 NTT Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.co.ntt.atrs.domain.service.c1;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.terasoluna.gfw.common.exception.SystemException;

import jp.co.ntt.atrs.domain.azure.helper.StorageAccountHelper;
import jp.co.ntt.atrs.domain.common.logging.LogMessages;
import jp.co.ntt.atrs.domain.model.Member;
import jp.co.ntt.atrs.domain.model.MemberLogin;
import jp.co.ntt.atrs.domain.repository.member.MemberRepository;

/**
 * 会員情報登録を行うService実装クラス。
 * @author NTT 電電花子
 */
@Service
@Transactional
public class MemberRegisterServiceImpl implements MemberRegisterService {

    /**
     * ストレージアカウントコンテナ名。
     */
    @Value("${upload.containerName}")
    String containerName;

    /**
     * ファイル一時保存ディレクトリ。
     */
    @Value("${upload.temporaryDirectory}")
    String tmpDirectory;

    /**
     * ファイル保存ディレクトリ。
     */
    @Value("${upload.saveDirectory}")
    String saveDirectory;
    
    /**
     * 会員情報リポジトリ。
     */
    @Inject
    MemberRepository memberRepository;

    /**
     * パスワードをハッシュ化するためのエンコーダ。
     */
    @Inject
    PasswordEncoder passwordEncoder;
    
    /**
     * StorageAccountHelper。
     */
    @Inject
    StorageAccountHelper storageAccountHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Member register(Member member) {

        Assert.notNull(member, "member must not null.");

        MemberLogin memberLogin = member.getMemberLogin();
        Assert.notNull(memberLogin, "memberLogin must not null.");

        // パスワードをエンコード
        String hashedPassword = passwordEncoder.encode(member.getMemberLogin()
                .getPassword());

        memberLogin.setPassword(hashedPassword);
        memberLogin.setLastPassword(hashedPassword);
        memberLogin.setLoginFlg(false);

        // 会員情報登録
        // (MyBatis3の機能(SelectKey)によりパラメータの会員情報に会員番号が格納される)
        int insertMemberCount = memberRepository.insert(member);
        if (insertMemberCount != 1) {
            throw new SystemException(LogMessages.E_AR_A0_L9002.getCode(), LogMessages.E_AR_A0_L9002
                    .getMessage(insertMemberCount, 1));
        }

        // 会員ログイン情報登録
        int insertMemberLoginCount = memberRepository.insertMemberLogin(member);
        if (insertMemberLoginCount != 1) {
            throw new SystemException(LogMessages.E_AR_A0_L9002.getCode(), LogMessages.E_AR_A0_L9002
                    .getMessage(insertMemberLoginCount, 1));
        }
        
        // ファイル保存を行う。
        String storageAccountPhotoFileName = member.getMembershipNumber() + "_" + UUID
                .randomUUID().toString() + ".jpg";
        storageAccountHelper.fileCopy(containerName, tmpDirectory, member.getPhotoFileName(),
        		containerName, saveDirectory, storageAccountPhotoFileName);

        storageAccountHelper.fileDelete(containerName, tmpDirectory, member
                .getPhotoFileName());

        // ストレージアカウントに保存した顔写真ファイル名をデータベースに登録する。
        member.setRegisteredPhotoFileName(storageAccountPhotoFileName);
        int updateMemberCount = memberRepository.update(member);
        if (updateMemberCount != 1) {
            throw new SystemException(LogMessages.E_AR_A0_L9002
                    .getCode(), LogMessages.E_AR_A0_L9002.getMessage(
                            updateMemberCount, 1));
        }

        return member;
    }

}
