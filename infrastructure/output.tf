output "vaultUri" {
  value = "${module.job-scheduler-vault.key_vault_uri}"
}

output "vaultName" {
  value = "${module.job-scheduler-vault.key_vault_name}"
}

output "microserviceName" {
  value = "${var.microservice}"
}
