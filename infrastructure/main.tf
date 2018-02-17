provider "azurerm" {}

provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

# Make sure the resource group exists
resource "azurerm_resource_group" "rg" {
  name = "${var.product}-${var.microservice}-${var.env}"
  location = "${var.location}"
}


locals {
  aseName = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
}

data "vault_generic_secret" "s2s_secret" {
  path = "secret/${var.env}/ccidam/service-auth-provider/api/microservice-keys/platformJobScheduler"
}

module "job-scheduler-database" {
  source = "git@github.com:contino/moj-module-postgres.git"
  product = "${var.product}-ase"
  location = "${var.location_db}"
  env = "${var.env}"
  postgresql_database = "${var.database-name}"
  postgresql_user = "jobscheduler"
}

module "job-scheduler-api" {
  source = "git@github.com:contino/moj-module-webapp.git"
  product = "${var.product}-${var.microservice}"
  location = "${var.location}"
  env = "${var.env}"
  ilbIp = "${var.ilbIp}"
  resource_group_name = "${azurerm_resource_group.rg.name}"

  app_settings = {
    // logging vars
    REFORM_TEAM = "${var.product}"
    REFORM_SERVICE_NAME = "${var.microservice}"
    REFORM_ENVIRONMENT = "${var.env}"

    // IdAM s2s
    S2S_URL = "${var.env == "prod" ? var.prod-s2s-url : var.test-s2s-url}"
    S2S_SECRET = "${data.vault_generic_secret.s2s_secret.data["value"]}"

    // db vars
    JOB_SCHEDULER_DB_HOST = "${module.job-scheduler-database.host_name}"
    JOB_SCHEDULER_DB_PORT = "${module.job-scheduler-database.postgresql_listen_port}"
    JOB_SCHEDULER_DB_PASSWORD = "${module.job-scheduler-database.postgresql_password}"
    JOB_SCHEDULER_DB_USERNAME = "${module.job-scheduler-database.user_name}"
    JOB_SCHEDULER_DB_NAME = "${module.job-scheduler-database.postgresql_database}"
    JOB_SCHEDULER_DB_CONNECTION_OPTIONS = "?ssl"
  }
}
