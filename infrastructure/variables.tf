variable "product" {
  type    = "string"
  default = "platform"
}

variable "microservice" {
  type    = "string"
  default = "job-scheduler"
}

variable "location" {
  type    = "string"
  default = "UK South"
}

variable "location_db" {
  type    = "string"
  default = "West Europe"
}

variable "env" {
  type    = "string"
}

variable "test-s2s-url" {
  type    = "string"
  default = "http://betaDevBccidamS2SLB.reform.hmcts.net"
}

variable "prod-s2s-url" {
  type    = "string"
  default = "http://betaProdccidamAppLB.reform.hmcts.net:4502"
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

variable "jenkins_AAD_objectId" {
  type                        = "string"
  description                 = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}
