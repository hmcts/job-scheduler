variable "product" {
  default = "platform"
}

variable "microservice" {
  default = "jobscheduler"
}

variable "location" {
  default = "UK South"
}

variable "env" {}

variable "test-s2s-url" {
  default = "http://betaDevBccidamS2SLB.reform.hmcts.net"
}

variable "prod-s2s-url" {
  default = "http://betaProdccidamAppLB.reform.hmcts.net:4502"
}


variable "database-name" {
  default = "postgres"
}

variable "ilbIp" {}

variable "component" {
  default = "backend"
}

variable "tenant_id" {
  description = "(Required) The Azure Active Directory tenant ID that should be used for authenticating requests to the key vault. This is usually sourced from environemnt variables and not normally required to be specified."
}

variable "client_id" {
  description = "(Required) The object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies. This is usually sourced from environment variables and not normally required to be specified."
}

variable "subscription" {}
