provider "azurerm" {}

provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

locals {
  vault_section = "${var.env == "prod" ? "prod" : "test"}"
}

data "vault_generic_secret" "s2s_secret" {
  path = "secret/${local.vault_section}/ccidam/service-auth-provider/api/microservice-keys/platformJobScheduler"
}

module "job-scheduler-database" {
  source              = "git@github.com:contino/moj-module-postgres.git?ref=master"
  product             = "${var.product}-${var.microservice}-db"
  location            = "${var.location_db}"
  env                 = "${var.env}"
  postgresql_user     = "jobscheduler"
}

module "job-scheduler-api" {
  source              = "git@github.com:contino/moj-module-webapp.git?ref=master"
  product             = "${var.product}-${var.microservice}"
  location            = "${var.location}"
  env                 = "${var.env}"
  ilbIp               = "${var.ilbIp}"

  app_settings = {
    // logging vars
    REFORM_TEAM         = "${var.product}"
    REFORM_SERVICE_NAME = "${var.microservice}"
    REFORM_ENVIRONMENT  = "${var.env}"

    // IdAM s2s
    S2S_URL     = "${var.env == "prod" ? var.prod-s2s-url : var.test-s2s-url}"
    S2S_SECRET  = "${data.vault_generic_secret.s2s_secret.data["value"]}"

    // db vars
    JOB_SCHEDULER_DB_HOST     = "${module.job-scheduler-database.host_name}"
    JOB_SCHEDULER_DB_PORT     = "${module.job-scheduler-database.postgresql_listen_port}"
    JOB_SCHEDULER_DB_PASSWORD = "${module.job-scheduler-database.postgresql_password}"
    JOB_SCHEDULER_DB_USERNAME = "${module.job-scheduler-database.user_name}"
    JOB_SCHEDULER_DB_NAME     = "postgres"
    JOB_SCHEDULER_DB_CONNECTION_OPTIONS = "?ssl"
  }
}

module "job-scheduler-vault" {
  source              = "git@github.com:contino/moj-module-key-vault?ref=master"
  name                = "job-scheduler-${var.env}"
  product             = "${var.product}-${var.microservice}-vault"
  env                 = "${var.env}"
  tenant_id           = "${var.tenant_id}"
  object_id           = "${var.jenkins_AAD_objectId}"
  resource_group_name = "${module.job-scheduler-api.resource_group_name}"
}
