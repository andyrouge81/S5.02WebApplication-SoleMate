package cat.itacademy.webappsolemate.infraestructure.security;

import cat.itacademy.webappsolemate.infraestructure.persistence.FootRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("footSecurity")
public class FootSecurity {

    private final FootRepository footRepository;

    public FootSecurity(FootRepository footRepository) {
        this.footRepository = footRepository;
    }

    public boolean isOwner(Long footId) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        System.out.println("AUTH USER = " + username);
        /*
        return footRepository.findById(footId)
                .map(foot -> foot.getOwner()
                        .getUsername()
                        .equals(username))
                .orElse(false);
                */
        return footRepository.findById(footId)
                .map(foot -> {
                    System.out.println("FOOT OWNER = " + foot.getOwner().getUsername());
                    return foot.getOwner().getUsername().equals(username);
                })
                .orElse(false);

    }
}
