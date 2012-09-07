var emr = (function($) {
	
	return {
		
		/**
		 * personName should be:
		 *   { givenName: "Darius", familyName: "Jazayeri", ... }
		 */
		formatPersonName: function(personName) {
			// TODO: don't hardcode this
			return personName.familyName + ", " + personName.givenName;
		},
		
		formatPreferredName: function(person) {
			return this.formatPersonName(person.preferredName);
		}

	};

})(jQuery);