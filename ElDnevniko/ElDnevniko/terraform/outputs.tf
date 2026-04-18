output "repo_url" {
  value = data.github_repository.spring_app.html_url
}

output "clone_url" {
  value = data.github_repository.spring_app.ssh_clone_url
}