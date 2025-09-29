package petwalk.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MeetingPlaceResponse {
    private MeetingCenterPoint meetingCenterPoint;
    private List<SearchResponse> recommendedPlaces;
}