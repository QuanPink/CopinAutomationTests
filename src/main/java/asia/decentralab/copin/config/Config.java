package asia.decentralab.copin.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Config {
    private String browser;
    private boolean headless;
    private String baseUrl;
    private String remoteHub;
}