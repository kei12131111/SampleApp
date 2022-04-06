package com.example.todo.api.common.error;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * REST API用エラー応答コントローラ。
 * <ul>
 * <li>サーブレットコンテナに通知されたエラーのエラー応答を行う。</li>
 * <ul>
 * @author NTT 電電太郎
 */
@RequestMapping("error")
@RestController
public class ApiErrorPageController {

    /**
     * APIエラー情報生成。
     */
    @Inject
    ApiErrorCreator apiErrorCreator;

    /**
     * HTTPステータス-エラーコードマップ。
     */
    private final Map<HttpStatus, String> errorCodeMap = new HashMap<HttpStatus, String>();

    /**
     * コンストラクタ。
     */
    public ApiErrorPageController() {
        errorCodeMap.put(HttpStatus.NOT_FOUND, "e.xx.fw.8001");
    }

    /**
     * エラー応答ハンドラ。
     * <ul>
     * <li>レスポンスコードによってエラーページのハンドリングを行う.</li>
     * </ul>
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<ApiError> handleErrorPage(WebRequest request) {
        HttpStatus httpStatus = HttpStatus.valueOf((Integer) request
                .getAttribute(RequestDispatcher.ERROR_STATUS_CODE,
                        RequestAttributes.SCOPE_REQUEST));
        String errorCode = errorCodeMap.get(httpStatus);
        ApiError apiError = apiErrorCreator.createApiError(request, errorCode,
                httpStatus.getReasonPhrase());

        return ResponseEntity.status(httpStatus).body(apiError);
    }
}