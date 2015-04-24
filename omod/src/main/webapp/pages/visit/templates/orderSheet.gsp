<div ng-hide="anyOrders()">
    None
</div>
<ul>
    <li ng-repeat="order in orderList()">
        {{ order.drug ? order.drug : order.concept | omrs.display }}
    </li>
</ul>