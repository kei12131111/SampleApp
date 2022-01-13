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
package jp.co.ntt.atrs.domain.service.c2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.terasoluna.gfw.common.exception.SystemException;

import jp.co.ntt.atrs.domain.azure.helper.StorageAccountHelper;
import jp.co.ntt.atrs.domain.common.exception.AtrsBusinessException;
import jp.co.ntt.atrs.domain.common.logging.LogMessages;
import jp.co.ntt.atrs.domain.common.util.ImageFileBase64Encoder;
import jp.co.ntt.atrs.domain.model.Member;
import jp.co.ntt.atrs.domain.model.MemberLogin;
import jp.co.ntt.atrs.domain.repository.member.MemberRepository;

/**
 * 会員情報変更を行うService実装クラス。
 * @author NTT 電電花子
 */
@Service
@Transactional
public class MemberUpdateServiceImpl implements MemberUpdateService {

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
     * 画像変換ユーティリティ。
     */
    @Inject
    ImageFileBase64Encoder imageFileBase64Encoder;

    /**
     * StorageAccountHelper。
     */
    @Inject
    StorageAccountHelper storageAccountHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Member findMember(String membershipNumber) throws IOException{

        Assert.hasText(membershipNumber, "membershipNumber must have some text.");

        Member member = memberRepository.findOne(membershipNumber);
        // 顔写真を取得する。
        Resource photoFileResource = storageAccountHelper.getResource(containerName,
                saveDirectory, member.getRegisteredPhotoFileName());
        if (photoFileResource.exists()) {
            // 顔写真が存在する場合は、顔写真データのBase64変換を行う。
            try (InputStream photoFile = photoFileResource.getInputStream()) {
                member.setPhotoBase64(imageFileBase64Encoder.encodeBase64(
                        photoFile, "jpg"));
            }
        }
        return member;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateMember(Member member) {

        Assert.notNull(member, "member must not null.");
        MemberLogin memberLogin = member.getMemberLogin();
        Assert.notNull(memberLogin, "memberLogin must not null.");

        // 会員情報更新
        int updateMemberCount = memberRepository.update(member);
        if (updateMemberCount != 1) {
            throw new SystemException(LogMessages.E_AR_A0_L9002.getCode(), LogMessages.E_AR_A0_L9002
                    .getMessage(updateMemberCount, 1));
        }

        // パスワードの変更がある場合のみ会員ログイン情報を更新
        if (StringUtils.hasLength(memberLogin.getPassword())) {

            // パスワードのハッシュ化
            memberLogin.setPassword(passwordEncoder.encode(member
                    .getMemberLogin().getPassword()));

            // 会員ログイン情報更新
            int updateMemberLoginCount = memberRepository
                    .updateMemberLogin(member);
            if (updateMemberLoginCount != 1) {
                throw new SystemException(LogMessages.E_AR_A0_L9002.getCode(), LogMessages.E_AR_A0_L9002
                        .getMessage(updateMemberLoginCount, 1));
            }
        }
        

        // 画像ファイルが選択されている場合のみ画像の更新を行う 。（photoFileNameの有無で判断する）
        if (member.getPhotoFileName() != null) {
            // ファイル保存を行う。
            // 削除対象旧ファイルの検索
            Resource[] oldPhotoResources = storageAccountHelper.fileSearch(containerName,
                    saveDirectory, member.getMembershipNumber() + "*");
            // 新規ファイル保存
            String storageAccountPhotoFileName = member.getMembershipNumber() + "_" + UUID
                    .randomUUID().toString() + ".jpg";
            storageAccountHelper.fileCopy(containerName, tmpDirectory, member
                    .getPhotoFileName(), containerName, saveDirectory,
                    storageAccountPhotoFileName);

            // 旧ファイルおよび一時ファイルの削除
    		List<String> fileList = new ArrayList<String>();		
            for (Resource oldPhotoResource : oldPhotoResources) {
            	fileList.add(oldPhotoResource.getFilename());
            }
        	storageAccountHelper.multiFileDelete("containerName", saveDirectory, fileList);

            // 顔写真ファイル名の更新を行う。
            member.setRegisteredPhotoFileName(storageAccountPhotoFileName);
            int updateCount = memberRepository.update(member);
            if (updateCount != 1) {
                throw new SystemException(LogMessages.E_AR_A0_L9002
                        .getCode(), LogMessages.E_AR_A0_L9002.getMessage(
                                updateCount, 1));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkMemberPassword(String password, String membershipNumber) {

        // パスワードの変更がある場合のみパスワードを比較
        if (StringUtils.hasLength(password)) {

            // 登録パスワードを取得
            Member member = memberRepository.findOne(membershipNumber);
            String currentPassword = member.getMemberLogin().getPassword();

            // パスワード不一致の場合、業務例外をスロー
            if (!passwordEncoder.matches(password, currentPassword)) {
                throw new AtrsBusinessException(MemberUpdateErrorCode.E_AR_C2_2001);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Member findMemberForLogin(String membershipNumber) {

        Assert.hasText(membershipNumber, "membershipNumber must have some text.");

        return memberRepository.findOneForLogin(membershipNumber);
    }

}
