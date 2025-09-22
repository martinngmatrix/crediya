package co.com.bancolombia.r2dbc.dto;

import lombok.Data;

@Data
public class SecretDTO {
  private String DB_NAME;
  private String DB_USER;
  private String DB_PASS;
  private String DB_HOST;
}
