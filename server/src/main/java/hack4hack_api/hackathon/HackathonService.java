package hack4hack_api.hackathon;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hack4hack_api.hackathon.dto.HackathonResponse;

@Service 
public class HackathonService {
    private final HackathonRepository hackathonRepository;

    public HackathonService(HackathonRepository hackathonRepository) {
        this.hackathonRepository = hackathonRepository;
    }

    @Transactional(readOnly = true)
    public HackathonResponse getHackathonById(UUID id){
        Hackathon hackathon = hackathonRepository.findById(id)
                                .orElseThrow(() -> new HackathonNotFoundException(id));
        return HackathonResponse.from(hackathon);
    }

}
