<#-- @ftlvariable type="judgels.michael.problem.programming.grading.EditAutomatonRestrictionView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <@forms.form>
    <@forms.formGroup>
      <@forms.formLabel value="Allowed automatons"/>
      <@forms.formField>
        <div class="checkbox">
          <label>
            <input
              type="checkbox"
              id="isAllowedAll"
              name="isAllowedAll"
              value="true"
              <#if formValues.isAllowedAll>checked</#if>
              <#if !canEdit>disabled</#if>
            > Allow all
          </label>
        </div>

        <#list automatons as automaton, name>
          <div class="checkbox">
            <label>
              <input type="checkbox"
                class="allowedAutomaton"
                name="allowedAutomatons"
                value="${automaton}"
                <#if formValues.allowedAutomatons?seq_contains(automaton)>checked</#if>
                <#if !canEdit>disabled</#if>
              > ${name}
            </label>
          </div>
        </#list>
      </@forms.formField>
    </@forms.formGroup>

    <#if canEdit>
      <@forms.submit>Update</@forms.submit>
      <script><#include "automatonRestriction.js"></script>
    </#if>
  </@forms.form>
</@template.layout>
