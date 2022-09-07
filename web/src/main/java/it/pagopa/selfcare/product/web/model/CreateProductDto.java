package it.pagopa.selfcare.product.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
public class CreateProductDto extends UpdateProductDto {

    @ApiModelProperty(value = "${swagger.product.model.id}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String id;

}
