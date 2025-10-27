package org.example.eatopia.common.infra.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 이 클래스가 Spring 설정 클래스임을 나타냅니다.
public class PortoneConfig {

    // application.yml 파일의 'portone.api-key' 값을 읽어와 apiKey 필드에 주입합니다.
    @Value("${portone.api-key}")
    private String apiKey;

    // application.yml 파일의 'portone.api-secret' 값을 읽어와 apiSecret 필드에 주입합니다.
    @Value("${portone.api-secret}")
    private String apiSecret;

    /**
     * IamportClient Bean을 생성하여 Spring 컨테이너에 등록합니다.
     * 이 Bean은 다른 서비스(예: PaymentCommandServiceImpl)에서 주입받아
     * PortOne API와 통신하는 데 사용됩니다.
     *
     * @return 설정된 API 키와 Secret 키로 초기화된 IamportClient 인스턴스
     */
    @Bean // 이 메서드가 반환하는 객체를 Bean으로 등록합니다.
    public IamportClient iamportClient() {
        // 주입받은 apiKey와 apiSecret을 사용하여 IamportClient 객체를 생성합니다.
        return new IamportClient(apiKey, apiSecret);
    }
}