## ADDED Requirements

### Requirement: Validate City Catalogs XML
The system SHALL validate the `city_catalogs.xml` configuration file using an XSD schema to ensure elements like `<catalogs>`, `<city>`, `<materials>`, `<supplies>`, and `<items>` are correctly defined.

#### Scenario: Server starts with city catalogs configuration
- **WHEN** the server starts and loads `city_catalogs.xml`
- **THEN** the XML parser validates the file against `city_catalogs.xsd` without reporting `cvc-elt.1.a` errors.

### Requirement: Validate Fake Shops XML
The system SHALL validate the `fake_shops.xml` configuration file using an XSD schema to ensure elements like `<shops>`, `<fakeshop>`, and `<customitem>` are correctly defined.

#### Scenario: Server starts with fake shops configuration
- **WHEN** the server starts and loads `fake_shops.xml`
- **THEN** the XML parser validates the file against `fake_shops.xsd` without reporting `cvc-elt.1.a` errors.
