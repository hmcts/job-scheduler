provider "vault" {
//  # It is strongly recommended to configure this provider through the
//  # environment variables described above, so that each user can have
//  # separate credentials set in the environment.
//  #
//  # This will default to using $VAULT_ADDR
//  # But can be set explicitly
  address = "https://vault.reform.hmcts.net:6200"
}

locals {
  aseName = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
  vault_section = "${var.env == "prod" ? "prod" : "test"}"

  s2s_url = "${var.env == "prod" ? var.prod-s2s-url : var.test-s2s-url}"
}

data "vault_generic_secret" "s2s_secret" {
  path = "secret/${local.vault_section}/ccidam/service-auth-provider/api/microservice-keys/platformJobScheduler"
}

module "job-scheduler-api" {
  source = "git@github.com:contino/moj-module-webapp.git"
  product = "${var.product}-${var.microservice}"
  location = "${var.location}"
  env = "${var.env}"
  ilbIp = "${var.ilbIp}"

  app_settings = {
    //    logging vars
    REFORM_TEAM = "${var.product}"
    REFORM_SERVICE_NAME = "${var.microservice}"
    REFORM_ENVIRONMENT = "${var.env}"

    // IdAM s2s
    S2S_URL = "${local.s2s_url}"
    S2S_SECRET = "${data.vault_generic_secret.s2s_secret.data["value"]}"

    // db vars
    JOB_SCHEDULER_DB_HOST = "${module.job-scheduler-database.host_name}"
    JOB_SCHEDULER_DB_PORT = "${module.job-scheduler-database.postgresql_listen_port}"
    JOB_SCHEDULER_DB_PASSWORD = "${module.job-scheduler-database.postgresql_password}"
    JOB_SCHEDULER_DB_USERNAME = "${module.job-scheduler-database.user_name}"
    JOB_SCHEDULER_DB_NAME = "${var.database-name}"
    JOB_SCHEDULER_DB_CONNECTION_OPTIONS = "?ssl"

    POSTGRES_DATABASE = "${module.job-scheduler-database.postgresql_database}"
  }
}

module "job-scheduler-database" {
  source = "git@github.com:contino/moj-module-postgres?ref=random-password"
  product = "${var.product}-ase"
  location = "West Europe"
  env = "${var.env}"
  postgresql_user = "jobscheduler"
  postgresql_database = "${var.database-name}"
}
