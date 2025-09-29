package petwalk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MeetingCenterPoint {
    private Double lat;
    private Double lng;
    private Integer participantCount;
}