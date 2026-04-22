resource "github_actions_secret" "docker_username" {
  repository = data.github_repository.spring_app.name
  secret_name     = "DOCKER_USERNAME"
  plaintext_value = data.vault_kv_secret_v2.terraform_secrets.data["docker_username"]
}

resource "github_actions_secret" "docker_password" {
  repository = data.github_repository.spring_app.name
  secret_name     = "DOCKER_PASSWORD"
  plaintext_value = data.vault_kv_secret_v2.terraform_secrets.data["docker_password"]
}
