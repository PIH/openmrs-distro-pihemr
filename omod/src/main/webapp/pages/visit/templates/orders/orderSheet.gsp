<div ng-hide="anyOrders()">
    None
</div>
<ul>
    <li ng-repeat="order in orderList()">
        {{ order.dateActivated | serverDate:dateFormat }}
        {{ order | orderInstructions }}
    </li>
</ul>