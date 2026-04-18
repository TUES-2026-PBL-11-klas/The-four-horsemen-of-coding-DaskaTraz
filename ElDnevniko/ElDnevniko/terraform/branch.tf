resource "github_branch_protection" "main" {
  repository_id = data.github_repository.spring_app.node_id
  pattern       = "main"

  required_pull_request_reviews {
    required_approving_review_count = 1
    dismiss_stale_reviews           = true
  }

  required_status_checks {
    strict   = true
    contexts = ["build-and-test"]
  }

  enforce_admins = false
}
