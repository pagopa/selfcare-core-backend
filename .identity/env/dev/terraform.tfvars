prefix    = "selc"
env       = "dev"
env_short = "d"
domain    = "ms-product"

cd_github_federations = [
  {
    repository = "selfcare-ms-product"
    subject    = "dev-cd"
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
  Environment = "Dev"
  Owner       = "SelfCare"
  Source      = "https://github.com/pagopa/selfcare-ms-product"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}
