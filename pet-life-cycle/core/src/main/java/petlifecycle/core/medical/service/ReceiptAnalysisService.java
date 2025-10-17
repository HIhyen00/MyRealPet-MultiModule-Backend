package petlifecycle.core.medical.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import petlifecycle.client.medical.response.ReceiptAnalysisResponse;
import petlifecycle.core.ai.service.OpenAIService;
import petlifecycle.core.metadata.service.FileService;
import petlifecycle.dto.metadata.entity.FileType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptAnalysisService {

    private final OpenAIService openAIService;
    private final FileService fileService;

    public ReceiptAnalysisResponse analyzeReceipt(MultipartFile file) {
        try {
            fileService.validateFile(file, FileType.MEDICAL_RECEIPT);
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String prompt = createSystemPrompt();
            String gptResponse = openAIService.analyzeReceiptImage(base64Image, prompt).block();
            return analysisResponse(gptResponse);
        } catch (Exception e) {
            log.error("청구서 분석 실패: {}", e.getMessage());
            throw new IllegalArgumentException("청구서 분석에 실패했습니다.", e);
        }
    }

    private String createSystemPrompt() {
        return """
                당신은 수의사 청구서 분석을 위한 전문 데이터 추출 AI입니다.
                사용자가 제공하는 이미지는 반려동물 병원의 진료 청구서입니다.
                이미지를 OCR 인식 없이 직접 분석하여, 아래 항목을 구조화된 JSON으로 반환하세요.
            
                ------------------------------------------------------------
                **목표**
                청구서 이미지를 분석하여 다음 정보를 추출합니다.

                1. hospitalName: 병원 이름
                2. hospitalNumber: 병원 전화번호
                3. hospitalAddress: 병원 주소
                4. visitDate: 진료 일자 (YYYY-MM-DD 형식)
                5. totalAmount: 총 금액 (숫자만)
                6. vatAmount: 부가세 (숫자만)
                7. testItems: 검사 내역 배열 [{name, quantity, unitPrice, amount, notes}]
                8. treatmentItems: 처치 내역 배열 [{name, quantity, unitPrice, amount, notes}]
                9. medicationItems: 처방 내역 배열 [{name, quantity, unitPrice, amount, frequency, days, notes}]

                ------------------------------------------------------------
                **세부 규칙**
                - 이미지를 직접 분석하여 문자나 숫자를 추출합니다. (OCR 도구 없음)
                - 모든 금액은 숫자만 포함하며, 콤마(,) 제거
                - 청구서에 표시된 금액은 "총 금액(amount)" 임
                - quantity가 있더라고 amount는 청구서에 표시된 그대로 사용
                - unitPrice = amount / quantity로 자동 계산 (예시: "병리검사 8회 96,000원 → quantity:8, amount:96000, unitPrice:12000)
                - quantity가 없으면 1로 표시
                - frequency는 "복용 빈도수"임. 표시된 경우에만 추출
                - days는 "처방 일수"임
                - (예시: 내복약 - 1일 2회(특수), quantity:1 → name:내복약(특수), frequency:1일 2회, days:1)
                - 날짜는 YYYY-MM-DD 형식으로 변환
                - 병원명, 주소, 전화번호가 명확하지 않으면 null
                - 항목 분류 기준:
                    * testItems: 검사, 혈액검사, 엑스레이, 초음파, 진찰료 등
                    * treatmentItems: 주사, 수액, 처치, 입원, 마취, 시술 등
                    * medicationItems: 약, 처방, 귀약, 안약, 연고 등
                - 항목 중 중복되거나 합쳐진 행은 가능한 한 분리하지 말고 단일 객체로 유지
                - notes는 항목 이름 외에 추가 설명이 있는 경우만 기입
                - diagnosis(진단명)는 청구서에 직접 표시된 경우에만 추출
                - 명시되지 않은 정보는 추정하지 말고 null로 반환
                - 최종 응답은 반드시 유효한 JSON 이어야 함
                - JSON 외의 불필요한 텍스트(설명, 마크다운 등)는 절대 포함하지 마세요.

                ------------------------------------------------------------
                 **응답 예시**
                {
                    "hospitalName": "리틀펫동물병원",
                    "hospitalNumber": "02-123-4567",
                    "hospitalAddress": "서울시 강남구 테헤란로 123",
                    "visitDate": "2025-10-02",
                    "totalAmount": 21000,
                    "vatAmount": 2385,
                    "testItems": [
                        {"name": "진찰 - 초진", "quantity": 1, "unitPrice": 9000, "amount": 9000, "notes": null}
                    ],
                    "treatmentItems": [],
                    "medicationItems": [
                        {"name": "귀 치료", "quantity": 1, "unitPrice": 12000, "amount": 12000, "notes": null, "frequency": null, "days": null}
                    ]
                }
                ------------------------------------------------------------
                이제 이미지를 분석하여 위 형식의 JSON만 반환하세요.
                응답은 반드시 순수 JSON 형태로만 반환하세요. 마크다운 코드 블록(```)을 사용하지 마세요.
            """;

    }

    private ReceiptAnalysisResponse analysisResponse(String gptResponse) {
        try {
            log.info("👀파싱 시작 - 원본 응답 길이: {}", gptResponse.length());
            log.info("👀파싱 시작 - 원본 응답 내용: {}", gptResponse);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            JsonNode node = mapper.readTree(gptResponse);

            String content;
            if (node.has("choices")) {
                content = node.get("choices").get(0).get("message").get("content").asText();
                log.info("👀추출된 content: {}", content);
            } else {
                content = gptResponse;
            }

            String cleanedContent = content
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();
            log.info("👀클리닝 후 content: {}", cleanedContent);
            return mapper.readValue(cleanedContent, ReceiptAnalysisResponse.class);

        } catch (JsonProcessingException e) {
            log.error("GPT 응답 파싱 실패: {}", e.getMessage());
            throw new IllegalArgumentException("청구서 분석 결과를 처리할 수 없습니다.");
        }
    }
}
