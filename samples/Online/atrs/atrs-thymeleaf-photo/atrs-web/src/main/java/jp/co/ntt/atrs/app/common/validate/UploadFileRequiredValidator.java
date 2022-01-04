package jp.co.ntt.atrs.app.common.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * ファイルアップロード必須チェック実行クラス
 */
public class UploadFileRequiredValidator implements
                                         ConstraintValidator<UploadFileRequired, MultipartFile> {

    /**
     * ファイルアップロード必須チェックアノテーション
     */
    private UploadFileRequired constraint;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(UploadFileRequired constraint) {
        this.constraint = constraint;
    }

    /**
     * ファイルアップロード必須チェックを行う
     * @param multipartFile アップロードファイル
     * @param context バリデータコンテキスト
     * @return チェック結果
     */
    @Override
    public boolean isValid(MultipartFile multipartFile,
            ConstraintValidatorContext context) {
        if (!constraint.check()) {
            return true;
        }
        return multipartFile != null && StringUtils.hasLength(multipartFile
                .getOriginalFilename());
    }

}