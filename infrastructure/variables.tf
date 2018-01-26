variable "product" {
  type    = "string"
  default = "job-scheduler"
}

variable "location" {
  type    = "string"
  default = "UK South"
}

variable "env" {
  type = "string"
}

variable "ilbIp"{}

variable "pg_user" {
  type = "string"
  default = "jobscheduler"
}

variable "pg_pass" {
  type = "string"
  default = "jobscheduler"
}

variable "s2s_url" {
  type = "string"
  default = "http://betaDevBccidamAppLB.reform.hmcts.net:4552"
}

variable "s2s_name" {
  type = "string"
  default = "jobscheduler"
}

variable "s2s_secret" {
  type = "string"
  default = ""
}
