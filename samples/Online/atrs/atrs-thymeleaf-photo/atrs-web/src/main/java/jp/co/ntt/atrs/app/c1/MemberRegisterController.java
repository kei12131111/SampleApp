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
package jp.co.ntt.atrs.app.c1;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.groups.Default;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenCheck;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenType;

import jp.co.ntt.atrs.app.c0.MemberHelper;
import jp.co.ntt.atrs.app.c2.MemberUpdateForm.UploadFileCheck;
import jp.co.ntt.atrs.app.c2.MemberUpdateForm.UploadFileUncheck;
import jp.co.ntt.atrs.app.common.exception.BadRequestException;
import jp.co.ntt.atrs.domain.azure.helper.StorageAccountHelper;
import jp.co.ntt.atrs.domain.common.util.ImageFileBase64Encoder;
import jp.co.ntt.atrs.domain.model.Member;
import jp.co.ntt.atrs.domain.service.c1.MemberRegisterService;

/**
 * 会員情報登録コントローラ。
 * @author NTT 電電花子
 */
@Controller
@RequestMapping("member/register")
@TransactionTokenCheck("member/register")
public class MemberRegisterController {

    /**
     * ストレージアカウントコンテナ名。
     */
    @Value("${upload.containerName}")
    String containerName;

    /**
     * ファイル一時保存ディレクトリ。
     */
    @Value("${upload.temporaryDirectory}")
    private String tmpDirectory;
	
    /**
     * 会員情報登録サービス。
     */
    @Inject
    MemberRegisterService memberRegisterService;

    /**
     * 会員情報Helper。
     */
    @Inject
    MemberHelper memberHelper;

    /**
     * 会員情報登録フォームのバリデータ。
     */
    @Inject
    MemberRegisterValidator memberRegisterValidator;

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
     * 会員情報登録フォームのバリデータをバインダに追加する。
     * @param binder バインダ
     */
    @InitBinder("memberRegisterForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(memberRegisterValidator);
    }

    /**
     * 会員情報登録フォームを初期化する。
     * @return 会員情報登録フォーム
     */
    @ModelAttribute("memberRegisterForm")
    public MemberRegisterForm setUpForm() {
        MemberRegisterForm memberRegisterForm = new MemberRegisterForm();
        return memberRegisterForm;
    }

    /**
     * 会員情報登録画面を表示する。
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @RequestMapping(method = RequestMethod.GET, params = "form")
    public String registerForm(Model model) {

        // カレンダー表示制御のため、生年月日入力可能日付を設定
        model.addAttribute("dateOfBirthMinDate", memberHelper
                .getDateOfBirthMinDate());
        model.addAttribute("dateOfBirthMaxDate", memberHelper
                .getDateOfBirthMaxDate());

        return "C1/memberRegisterForm";
    }

    /**
     * 会員登録確認画面を表示する。
     * <ul>
     * <li>トランザクショントークンチェックのトランザクションを開始する。</li>
     * <li>チェックエラーがある場合、会員情報登録画面を再表示する。</li>
     * <li>チェックOKの場合、会員登録確認画面を表示する。</li>
     * </ul>
     * @param memberRegisterForm 会員情報登録フォーム
     * @param result チェック結果
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @TransactionTokenCheck(type = TransactionTokenType.BEGIN)
    @RequestMapping(method = RequestMethod.POST, params = "confirm")
    public String registerConfirm(@Validated({ UploadFileCheck.class,
            Default.class }) MemberRegisterForm memberRegisterForm,
            BindingResult result, Model model) throws IOException {

        if (result.hasErrors()) {
            // 検証エラーがある場合は画面再表示
            return registerRedo(memberRegisterForm, model);
        }
        
        String originalFilename = memberRegisterForm.getPhoto()
                .getOriginalFilename();
        String fileExtension = FilenameUtils.getExtension(originalFilename);

        // ファイル一時保存
        MultipartFile uploadFile = memberRegisterForm.getPhoto();
        String photoFileName = UUID.randomUUID().toString() + FilenameUtils
                .getName(originalFilename);
        try (InputStream inputStream = uploadFile.getInputStream()) {
        	storageAccountHelper.fileUpload(inputStream, containerName, tmpDirectory,
                    photoFileName);
        }

        // ファイル名をformに設定
        memberRegisterForm.setPhotoFileName(photoFileName);

        // 画像をエンコードしてFormに設定
        try (InputStream photoFile = uploadFile.getInputStream()) {
            memberRegisterForm.setPhotoBase64(
                    imageFileBase64Encoder.encodeBase64(photoFile,
                            fileExtension));
        }

        return "C1/memberRegisterConfirm";
    }

    /**
     * 会員情報登録画面を再表示する。
     * @param memberRegisterForm 会員情報登録フォーム
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @RequestMapping(method = RequestMethod.POST, params = "redo")
    public String registerRedo(MemberRegisterForm memberRegisterForm,
            Model model) {

        // カレンダー表示制御のため、生年月日入力可能日付を設定
        model.addAttribute("dateOfBirthMinDate", memberHelper
                .getDateOfBirthMinDate());
        model.addAttribute("dateOfBirthMaxDate", memberHelper
                .getDateOfBirthMaxDate());

        return "C1/memberRegisterForm";
    }

    /**
     * 会員情報を登録する。
     * <ul>
     * <li>トランザクショントークンチェックを行う。</li>
     * <li>登録後、会員登録完了画面をリダイレクトで表示する。</li>
     * </ul>
     * @param memberRegisterForm 会員情報登録フォーム
     * @param model 出力情報を保持するクラス
     * @param result チェック結果
     * @param redirectAttributes フラッシュスコープ格納用オブジェクト
     * @return View論理名
     */
    @TransactionTokenCheck
    @RequestMapping(method = RequestMethod.POST)
    public String register(@Validated({ UploadFileUncheck.class,
            Default.class }) MemberRegisterForm memberRegisterForm,
            BindingResult result, Model model,
            RedirectAttributes redirectAttributes) throws IOException {

        if (result.hasErrors()) {
            // 非表示項目に検証エラーがある場合は改ざんとみなす
            throw new BadRequestException(result);
        }

        // 会員情報登録(返却された会員情報に会員番号が格納されている)
        Member member = memberHelper.toMember(memberRegisterForm);
        
        // DBへの登録およびファイル保存を行う。
        member = memberRegisterService.register(member);

        // 会員情報をリダイレクト情報に設定
        redirectAttributes.addFlashAttribute(member);

        // 会員登録完了画面へリダイレクト
        return "redirect:/member/register?complete";
    }

    /**
     * 会員登録完了画面を表示する。
     * @return View論理名
     */
    @RequestMapping(method = RequestMethod.GET, params = "complete")
    public String registerComplete() {

        return "C1/memberRegisterComplete";
    }

}
