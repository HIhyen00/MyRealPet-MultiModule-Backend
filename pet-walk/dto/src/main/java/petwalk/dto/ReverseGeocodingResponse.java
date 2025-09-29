package petwalk.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReverseGeocodingResponse {
    private ReverseMeta meta;
    private List<ReverseDocument> documents;

}