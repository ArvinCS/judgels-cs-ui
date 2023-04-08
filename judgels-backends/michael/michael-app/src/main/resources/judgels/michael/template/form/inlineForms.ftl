<#import "/judgels/michael/template/ui/buttons.ftl" as buttons>

<#macro form action="">
  <form method="POST" class="form-inline" <#if action?has_content>action="${action}"</#if>>
    <#nested>
  </form>
</#macro>

<#macro select name label options form={}>
  <div class="form-group">
    <label for="${name}"><span class="small">${label}</span></label>
    <select id="${name}" name="${name}">
      <#list options as k, v>
        <option value="${k}" ${(k == form[name]!"")?then("selected", "")}>${v}</option>
      </#list>
    </select>
  </div>
</#macro>

<#macro submit>
  <@buttons.submit size="xs">
    <#nested>
  </@buttons.submit>
</#macro>
