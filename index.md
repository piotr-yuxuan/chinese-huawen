---
layout: default
---
{% for post in site.posts reversed %}{{ post.title | prepend: "<!--" }}{% if post.abstract %} Abstract -->
* [{{ post.title }}]({{ post.url | remove: '/' | prepend: site.baseurl }}) {{ post.abstract }}
{% else %} Content -->

{{ post.content }}{% endif %}{% endfor %}

