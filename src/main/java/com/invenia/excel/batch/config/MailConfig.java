package com.invenia.excel.batch.config;

import java.util.LinkedList;
import java.util.List;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@ToString
@EqualsAndHashCode
@ConfigurationProperties("mail")
@Slf4j
public class MailConfig {

  private List<String> recipients;
  private List<String> carbonCopies;

  public Address[] getDefaultRecipients() {
    return getAddresses(recipients);
  }

  public Address[] getDefaultCarbonCopies() {
    return getAddresses(carbonCopies);
  }

  private Address[] getAddresses(List<String> list) {
    List<Address> addresses = new LinkedList<>();
    if (list != null) {
      for (String email : list) {
        try {
          addresses.add(new InternetAddress(email));
        } catch (AddressException e) {
          log.error(e.getLocalizedMessage(), e);
        }
      }
    }
    return addresses.toArray(new Address[0]);
  }
}
