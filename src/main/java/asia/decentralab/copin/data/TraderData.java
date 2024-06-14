package asia.decentralab.copin.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TraderData {
    public String validAddress;
    public String inValidAddress;
    public String validTxHash;
    public String inValidTxHash;
}
