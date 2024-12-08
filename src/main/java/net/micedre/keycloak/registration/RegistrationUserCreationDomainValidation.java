package net.micedre.keycloak.registration;

import org.jboss.logging.Logger;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.authentication.forms.RegistrationUserCreation;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;

import jakarta.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

public abstract class RegistrationUserCreationDomainValidation extends RegistrationUserCreation implements FormAction {
   protected static final Logger logger = Logger.getLogger(RegistrationUserCreationDomainValidation.class);

   protected static final String DEFAULT_DOMAIN_LIST = "example.org";
   protected static final String DOMAIN_LIST_SEPARATOR = "(?:\\r\\n|\\r|\\n|##)";

   @Override
   public boolean isConfigurable() {
        return true;
   }

   @Override
   public void success(FormContext context) {
   }

   protected static final boolean globmatches(String text, String glob) {
      if (text.length() > 200) {
         return false;
      }
      String rest = null;
      int pos = glob.indexOf('*');
      if (pos != -1) {
         rest = glob.substring(pos + 1);
         glob = glob.substring(0, pos);
      }

      if (glob.length() > text.length())
         return false;

      // handle the part up to the first *
      for (int i = 0; i < glob.length(); i++)
         if (glob.charAt(i) != '?'
                 && !glob.substring(i, i + 1).equalsIgnoreCase(text.substring(i, i + 1)))
            return false;

      // recurse for the part after the first *, if any
      if (rest == null) {
         return glob.length() == text.length();
      } else {
         for (int i = glob.length(); i <= text.length(); i++) {
            if (globmatches(text.substring(i), rest))
                return true;
         }
         return false;
      }
   }

   @Override
   public void validate(ValidationContext context) {
      MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

      List<FormMessage> errors = new ArrayList<>();
      String email = formData.getFirst(Validation.FIELD_EMAIL);

      AuthenticatorConfigModel mailDomainConfig = context.getAuthenticatorConfig();
      String eventError = Errors.INVALID_REGISTRATION;

      if(email == null){
         context.getEvent().detail(Details.EMAIL, email);
         errors.add(new FormMessage(RegistrationPage.FIELD_EMAIL, Messages.INVALID_EMAIL));
         context.error(eventError);
         context.validationError(formData, errors);
         return;
      }

      String[] domainList = getDomainList(mailDomainConfig);

      boolean emailDomainValid = isEmailValid(email, domainList);

      if (!emailDomainValid) {
         context.getEvent().detail(Details.EMAIL, email);
         errors.add(new FormMessage(RegistrationPage.FIELD_EMAIL, Messages.INVALID_EMAIL));
      }
      if (errors.size() > 0) {
         context.error(eventError);
         context.validationError(formData, errors);
      } else {
         context.success();
      }
   }

   public abstract String[] getDomainList(AuthenticatorConfigModel mailDomainConfig);

   public abstract boolean isEmailValid(String email, String[] domains);
}

