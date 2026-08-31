## Context

The L2Journey gameserver uses an XML parser (`IXmlReader`) that enforces schema validation by default. The newly introduced Fake Players system uses two XML configuration files (`city_catalogs.xml` and `fake_shops.xml`) which currently lack an XSD schema definition. As a result, the parser emits warnings on server startup because it cannot validate these files. 

## Goals / Non-Goals

**Goals:**
- Eliminate the `cvc-elt.1.a: Cannot find the declaration of element` warnings for both fake player XML files.
- Ensure that the schemas accurately reflect the structure expected by `FakeShopData.java`.

**Non-Goals:**
- We are not refactoring the `FakeShopData` parser logic.
- We are not turning off XML validation in `IXmlReader` or `FakeShopData` since having schemas is a better long-term practice.

## Decisions

- Create two separate XSD files (`city_catalogs.xsd` and `fake_shops.xsd`) in the `dist/game/data/xsd/` directory.
- Use `xs:element` and `xs:attribute` definitions to mirror the elements loaded in `FakeShopData.java`.
- Link the XSD files inside the respective XML files using `xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"` and `xsi:noNamespaceSchemaLocation="../xsd/<file>.xsd"`.

## Risks / Trade-offs

- **Risk**: Strict validation might cause the server to fail to start if the XML files have slight deviations from the schema.
- **Mitigation**: The schemas will be designed to be flexible enough to accommodate current structures (e.g., using `minOccurs="0"` and allowing arbitrary ordering where necessary).
