package com.example.win.service;

import com.example.win.entity.GeneratedWelfare;
import com.example.win.repository.GeneratedWelfareRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GeneratedWelfareService {

    private final GptWelfareAutoService gptWelfareAutoService;
    private final GeneratedWelfareRepository generatedWelfareRepository;

    public List<GeneratedWelfare> generateAndSave() {
        String prompt = """
한국의 복지 관련 정책과 지원 제도 중에서, 미취학 아동, 장애인, 청소년, 고령자, 저소득층 등에게 도움이 되는 실제 정책/사업을 찾아서 10개를 아래 형식으로 정리해줘.

각 항목은 반드시 아래 5가지 키를 포함해야 해:

- "title": 지원 제도의 이름
- "description": 해당 정책의 핵심 내용을 발달장애인도 이해할 수 있도록 아주 쉬운 말로 1~2문장으로 설명 (존댓말 사용)
- "keywords": 정책과 관련된 핵심 키워드 3~4개 (예: 청소년, 문화카드, 영화, 공연)
- "link": 해당 정책의 링크 (실존하는 실제 링크여야 하며, 없다면 "링크 없음"으로 표시)
- "image": 해당 정책의 배너나 공식 이미지 (없으면 "이미지 없음"으로 표시)

결과는 반드시 JSON 배열 형식으로만 보여줘.
예시:
[
  {
    "title": "청소년 문화누리카드",
    "description": "청소년이 영화나 공연을 무료로 볼 수 있도록 도와주는 카드예요.",
    "keywords": ["청소년", "문화", "영화", "공연"],
    "link": "https://www.munhwanuricard.kr",
    "image": "https://example.com/banner.jpg"
  }
]
""";

        String response = gptWelfareAutoService.complete(prompt);
        JSONArray jsonArray = new JSONArray(response);

        List<GeneratedWelfare> list = IntStream.range(0, jsonArray.length())
                .mapToObj(i -> {
                    JSONObject item = jsonArray.getJSONObject(i);
                    String link = item.optString("link", null);
                    String image = item.optString("image", null);

                    if ("링크 없음".equals(link)) link = null;
                    if ("이미지 없음".equals(image)) image = null;

                    List<String> keywordList = IntStream.range(0, item.getJSONArray("keywords").length())
                            .mapToObj(j -> item.getJSONArray("keywords").getString(j))
                            .toList();

                    return GeneratedWelfare.builder()
                            .title(item.optString("title", "제목 없음"))
                            .description(item.optString("description", "설명 없음"))
                            .keywords(String.join(",", keywordList))
                            .link(link)
                            .image(image)
                            .build();
                })
                .collect(Collectors.toList());

        return generatedWelfareRepository.saveAll(list);
    }
}
