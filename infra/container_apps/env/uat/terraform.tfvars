env_short = "u"

tags = {
  CreatedBy   = "Terraform"
  Environment = "Uat"
  Owner       = "SelfCare"
  Source      = "https://github.com/pagopa/selfcare-ms-product"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}

container_app = {
  min_replicas = 1
  max_replicas = 2
  scale_rules  = []
  cpu          = 0.5
  memory       = "1Gi"
}



app_settings = [
  {
    name  = "APPLICATIONINSIGHTS_ROLE_NAME"
    value = "ms-product",
  },
  {
    name  = "JAVA_TOOL_OPTIONS"
    value = "-javaagent:applicationinsights-agent.jar",
  },
  {
    name  = "APPLICATIONINSIGHTS_INSTRUMENTATION_LOGGING_LEVEL"
    value = "OFF",
  },
  {
    name  = "LOGO_STORAGE_URL"
    value = "https://selcucheckoutsa.z6.web.core.windows.net/resources/products/default/logo.png"
  },
  {
    name  = "DEPICT_IMAGE_URL"
    value =  "https://selcucheckoutsa.z6.web.core.windows.net/resources/products/default/depict-image.jpeg"
  },
  {
    name = "BLOBSTORAGE_PUBLIC_HOST"
    value = "selcucheckoutsa.z6.web.core.windows.net"
  }
]

secrets_names = {
  "JWT_TOKEN_PUBLIC_KEY"                    = "jwt-public-key"
  "MONGODB_CONNECTION_URI"                  = "mongodb-connection-string"
  "BLOB_STORAGE_CONN_STRING"                = "blob-storage-product-connection-string"
  "APPLICATIONINSIGHTS_CONNECTION_STRING"   = "appinsights-connection-string"
}
