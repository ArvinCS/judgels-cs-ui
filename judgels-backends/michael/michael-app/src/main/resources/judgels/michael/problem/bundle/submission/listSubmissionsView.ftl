<#-- @ftlvariable type="judgels.michael.problem.bundle.submission.ListSubmissionsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/table/tableLayout.ftl" as table>
<#import "/judgels/michael/template/table/paginationView.ftl" as pagination>

<@template.layout>
  <#if canEdit>
    <a type="button" class="btn btn-primary btn-sm" style="margin-bottom: 10px" href="submissions/regrade" <#if !canSubmit>disabled</#if> onclick="return confirm('Will regrade ALL submissions in ALL pages, are you sure?')">
      <span class="glyphicon glyphicon-refresh"></span> Regrade all
    </a>
  </#if>

  <@table.layout>
    <thead>
      <tr>
        <th style="min-width: 50px">ID</th>
        <th>Author</th>
        <th>Score</th>
        <th>Submitted at</th>
        <th class="col-fit"></th>
      </tr>
    </thead>
    <tbody>
      <#list submissions.page as submission>
        <tr>
          <td class="col-fit">${submission.id}</td>
          <td>${profilesMap[submission.authorJid].username}</td>
          <td>${submission.latestGrading.score}</td>
          <td>${getFormattedDurationFromNow(submission.time)}</td>
          <td class="col-fit">
            <a type="button" class="btn btn-primary btn-xs" href="submissions/${submission.id}">View</a>
            <#if canEdit>
              <a type="button" class="btn btn-default btn-xs" href="submissions/${submission.id}/regrade" <#if !canSubmit>disabled</#if>>
                <span class="glyphicon glyphicon-refresh"></span>
              </a>
            </#if>
          </td>
        </tr>
      </#list>
    </tbody>
  </@table.layout>
  <@pagination.view page=submissions/>
</@template.layout>
