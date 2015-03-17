Allergies:
<span ng-show="allergies.status == 'Unknown'">?</span>
<span ng-show="allergies.status == 'See list'" ng-repeate="allergy in allergies">
    {{ allergy | omrs.display }}
</span>