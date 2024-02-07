prefix    = "selc"
env       = "prod"
env_short = "p"
domain    = "ms-product"

cd_github_federations = [
  {
    repository = "selfcare-ms-product"
    subject    = "prod-cd"
  }
]

environment_cd_roles = {
  subscription    = ["Contributor"]
  resource_groups = {
    "terraform-state-rg" = [
      "Storage Blob Data Contributor"
    ]
  }
}

tags = {
  CreatedBy   = "Terraform"
  Environment = "Prod"
  Owner       = "SelfCare"
  Source      = "https://github.com/pagopa/selfcare-infra"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}
