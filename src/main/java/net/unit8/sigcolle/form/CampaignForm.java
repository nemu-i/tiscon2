package net.unit8.sigcolle.form;


import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author kawasima
 */
@Data
public class CampaignForm extends FormBase {
    @DecimalMin("1")
    @DecimalMax("9999")

    @NotNull
    private String campaignId;

    @NotNull
    @Length(min = 1, max = 20)
    private String title;

    // Markdown description
    @NotNull
    @Length(min = 1, max = 20)
    private String statement;

    @NotNull
    //@Mask(mask = ".*[^0-9].*", msg = @Msg(key = "errors.existNoDigit"))
    private Long goal;

    @NotNull
    private Long createUserId;

    public Long getCampaignIdLong() {
        return Long.parseLong(campaignId);
    }

    @Override
    public boolean hasErrors() {
        return super.hasErrors();
    }

    @Override
    public boolean hasErrors(String name) {
        return super.hasErrors(name);
    }

    @Override
    public List<String> getErrors(String name) {
        return super.getErrors(name);
    }
}
