# Git Quick Command Cheat Sheet

git clone <repo_url>
Use when: downloading an existing repository from GitHub to local system.

git init
Use when: starting Git in a new local project.

git status
Use when: checking which files are modified, staged, or untracked.

git add file_name
Use when: adding a specific file to staging before commit.

git add .
Use when: adding all changed files to staging.

git commit -m "message"
Use when: saving staged changes with a message.

git branch
Use when: seeing all branches in the repository.

git branch branch_name
Use when: creating a new branch.

git checkout branch_name
Use when: switching to another branch.

git checkout -b branch_name
Use when: creating and switching to a new branch in one step.

git push origin branch_name
Use when: uploading commits from local branch to GitHub.

git push -u origin branch_name
Use when: pushing a new branch to GitHub for the first time.

git pull origin main
Use when: getting the latest changes from the remote repository.

git fetch
Use when: downloading remote changes without merging them.

git merge branch_name
Use when: combining another branch into the current branch.

git rebase branch_name
Use when: updating your branch with latest commits while keeping history linear.

git log
Use when: viewing detailed commit history.

git log --oneline
Use when: seeing a short version of commit history.

git diff
Use when: checking changes in files before committing.

git restore file_name
Use when: discarding changes in a file.

git restore --staged file_name
Use when: removing a file from staging area.

git revert commit_id
Use when: undoing a commit safely while keeping history.

git reset --soft HEAD~1
Use when: removing last commit but keeping the changes.

git reset --hard HEAD~1
Use when: removing last commit and deleting changes completely.

git remote -v
Use when: checking the remote repository connected to local repo.
