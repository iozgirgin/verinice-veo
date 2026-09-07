# Content deployment

Publish the domain INSERT_DOMAIN_NAME_HERE on INSERT_PROD_OR_JUST_SANDBOX_HERE.

# Implementation notes

1. Apply the **current milestone** to this issue (so we'll have three full weeks to test the content).
2. Find the **source domain** in the content creation client on stage.
3. If necessary, update all **catalog items** in the source domain.
4. If necessary, update all **profiles** in the source domain.
5. Ensure no **conflicts** with other domains are present.
6. If **breaking changes** are present, create migration steps.
7. Create a **domain template** from the domain.
8. Download the domain template and store it in the **content repository**.
9. Download the form template bundle and store it in the **content repository**.
10. Upload the domain template to **develop**.
11. Upload the form template bundle to **develop**.
12. Add content download links to the **deployment ticket** for the target milestone.

# Acceptance criteria

- [ ] The new content is available in the **vanilla test client on stage**.
- [ ] The new content is available on **develop**.
- [ ] Download links for the new content have been added to the deployment ticket.

---

/label ~"1.Component::3.Content"

/label ~"2.IssueType::1.Story"
