package asia.decentralab.copin.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BrowserOptionsConfig {
    private String[] chromeOptions;
    private String[] firefoxOptions;
    private String[] edgeOptions;
}