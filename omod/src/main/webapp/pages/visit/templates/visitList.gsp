<ul>
    <li ng-repeat="visit in visits">
        <a ng-click="goToVisit(visit)">{{ visit.display }}</a>
    </li>
</ul>