# Git Commands Notes

These are the basic Git commands I used and practiced during the training.

git clone <repo_url>  
Used to copy a repo from GitHub to my local system.

git init  
Used to start git in a new project.

git status  
Used to check which files are changed or not tracked.

git add file_name  
Used to add one file to staging.

git add .  
Used to add all files.

git commit -m "message"  
Used to save changes with a message.

git branch  
Used to see all branches.

git branch branch_name  
Used to create a new branch.

git checkout branch_name  
Used to switch branch.

git checkout -b branch_name  
Used to create and switch branch at same time.

git push origin branch_name  
Used to push code to GitHub.

git push -u origin branch_name  
Used first time push for a branch.

git pull origin main  
Used to get latest code from main branch.

git fetch  
Used to get updates without merging.

git merge branch_name  
Used to merge another branch.

git rebase branch_name  
Used to update branch and keep history clean.

git log  
Used to see commit history.

git log --oneline  
Used to see short history.

git diff  
Used to check changes before commit.

git restore file_name  
Used to discard changes in a file.

git restore --staged file_name  
Used to remove file from staging.

git revert commit_id  
Used to undo a commit safely.

git reset --soft HEAD~1  
Used to remove last commit but keep changes.

git reset --hard HEAD~1  
Used to remove last commit and delete changes.

git remote -v  
Used to check remote repo link.
