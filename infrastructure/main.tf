locals {
  default_s2s_url = "http://idam-s2s-${var.env}.service.${data.terraform_remote_state.core_apps_compute.ase_name[0]}.internal"
  s2s_url = "${var.s2s_url != "" ? var.s2s_url : local.default_s2s_url}"
}

resource "random_string" "password" {
  length = 32
  special = true
}

module "backend" {
  source   = "git@github.com:contino/moj-module-webapp?ref=master"
  product  = "${var.product}-backend"
  location = "${var.location}"
  env      = "${var.env}"
  ilbIp    = "${var.ilbIp}"

  app_settings = {
    JOB_SCHEDULER_DB_HOST     = "${module.database.host_name}"
    JOB_SCHEDULER_DB_PORT     = "${module.database.postgresql_listen_port}"
    JOB_SCHEDULER_DB_PASSWORD = "${module.database.postgresql_password}"
    S2S_URL                   = "${local.s2s_url}"
    S2S_SECRET                = "${var.s2s_secret}" # TODO hardcode shared value?
    S2S_NAME                  = "${var.s2s_name}"
  }
}

module "database" {
  source              = "git@github.com:contino/moj-module-postgres?ref=master"
  product             = "${var.product}-combined"
  location            = "West Europe"
  env                 = "${var.env}"
  postgresql_user     = "${var.pg_user}"
  postgresql_password = "${random_string.password.result}"
}
