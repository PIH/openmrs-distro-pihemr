<a ng-if="action.href" class="button task" href="{{ eval(action.href) }}">
    <i ng-show="action.icon" class="{{ action.icon }}"></i>
    {{ action.label | omrs.display }}
</a>
<a ng-if="action.sref" class="button task" ui-sref="{{ action.sref }}">
    <i ng-show="action.icon" class="{{ action.icon }}"></i>
    {{ action.label | omrs.display }}
</a>