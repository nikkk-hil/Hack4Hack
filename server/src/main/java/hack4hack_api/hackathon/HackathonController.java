package hack4hack_api.hackathon;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hack4hack_api.hackathon.dto.HackathonResponse;

@RestController 
@RequestMapping("/api/v1/hackathons")
public class HackathonController {

    private final HackathonService hackathonService;

    public HackathonController(HackathonService hackathonService){
        this.hackathonService = hackathonService;
    }

    @GetMapping("/{id}")
    public HackathonResponse getHackathon(@PathVariable UUID id){
        return hackathonService.getHackathonById(id);
    }

}
