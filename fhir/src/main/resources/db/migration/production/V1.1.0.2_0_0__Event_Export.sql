/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO PROGRAM_STAGE_EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

-- @formatter:off

INSERT INTO fhir_script_variable_enum VALUES('TEI_FHIR_RESOURCE');

ALTER TABLE fhir_system ADD fhir_display_name VARCHAR(100);
COMMENT ON COLUMN fhir_system.fhir_display_name IS 'Display name of the system and its assigned identifiers and codes that is used when storing data on FHIR servers.';

UPDATE fhir_system SET fhir_display_name='CVX' WHERE system_uri='http://hl7.org/fhir/sid/cvx';
UPDATE fhir_system SET fhir_display_name='LOINC' WHERE system_uri='http://loinc.org';
UPDATE fhir_system SET fhir_display_name='SNOMED CT' WHERE system_uri='http://snomed.info/sct';
UPDATE fhir_system SET fhir_display_name='FHIR Personal Relationship Role Type V2' WHERE system_uri='http://hl7.org/fhir/v2/0131';
UPDATE fhir_system SET fhir_display_name='FHIR Personal Relationship Role Type V3' WHERE system_uri='http://hl7.org/fhir/v3/RoleCode';
UPDATE fhir_system SET fhir_display_name='DHIS2 FHIR ID' WHERE system_uri='http://www.dhis2.org/dhis2-fhir-adapter/systems/identifier';

UPDATE fhir_system SET system_uri='http://www.dhis2.org/dhis2-fhir-adapter/systems/DHIS2-FHIR-Identifier' WHERE system_uri='http://www.dhis2.org/dhis2-fhir-adapter/systems/identifier';

INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('aa665bc7-631a-4b6e-865d-115e18f8bf1b', 0, '53fdf1da-6145-4e25-9d25-fc41b9baf09f',
'facilityTypeDisplayName', 'STRING', FALSE, 'Facility', 'The optional display name for the facility type.');
UPDATE fhir_script_source SET source_text =
'output.setPartOf(null);
output.getType().clear();
if ((input.getLevel() > args[''facilityLevel'']) && (input.getParentId() != null))
{
  var parentOrganizationUnit = organizationUnitResolver.getMandatoryById(input.getParentId());
  var parentOrganization = organizationUnitResolver.getFhirResource(parentOrganizationUnit);
  if (parentOrganization == null)
  {
    context.missingDhisResource(parentOrganizationUnit.getResourceId());
  }
  output.getPartOf().setReferenceElement(parentOrganization.getIdElement().toUnqualifiedVersionless());
}
else
{
  output.addType().setText(args[''facilityTypeDisplayName'']).addCoding().setSystem(''http://hl7.org/fhir/organization-type'').setCode(''prov'');
}
output.setActive(input.getClosedDate() == null);
output.setName(input.getName());
output.getAlias().clear();
if ((input.getShortName() != null) && !input.getShortName().equals(input.getName()))
{
  output.addAlias(input.getShortName());
}
if ((input.getDisplayName() != null) && !input.getDisplayName().equals(input.getName()) && !fhirResourceUtils.containsString(output.getAlias(), input.getDisplayName()))
{
  output.addAlias(input.getDisplayName());
}
true' WHERE id='50544af8-cf52-4e1e-9a5d-d44c736bc8d8';

UPDATE fhir_script_source SET source_text =
'function canOverrideAddress(address)
{
  return !address.hasLine() && !address.hasCity() && !address.hasDistrict() && !address.hasState() && !address.hasPostalCode() && !address.hasCountry();
}
if (output.getName().size() < 2)
{
  var lastName = input.getValue(args[''lastNameAttribute'']);
  var firstName = input.getValue(args[''firstNameAttribute'']);
  if ((lastName != null) || (firstName != null) || args[''resetFhirValue''])
  {
    output.getName().clear();
    if ((lastName != null) || args[''resetFhirValue''])
    {
      output.getNameFirstRep().setFamily(lastName);
    }
    if ((firstName != null) || args[''resetFhirValue''])
    {
      humanNameUtils.updateGiven(output.getNameFirstRep(), firstName);
    }
  }
}
if (args[''birthDateAttribute''] != null)
{
  var birthDate = input.getValue(args[''birthDateAttribute'']);
  if ((birthDate != null) || args[''resetFhirValue''])
  {
    output.setBirthDateElement(dateTimeUtils.getPreciseDateElement(birthDate));
  }
}
if (args[''genderAttribute''] != null)
{
  var gender = input.getValue(args[''genderAttribute'']);
  if ((gender != null) || args[''resetFhirValue''])
  {
    output.setGender(genderUtils.getAdministrativeGender(gender));
  }
}
if ((args[''addressTextAttribute''] != null) && (output.getAddress().size() < 2))
{
  var addressText = input.getValue(args[''addressTextAttribute'']);
  if (((addressText != null) || args[''resetFhirValue'']) && (args[''resetAddressText''] || !output.hasAddress() || canOverrideAddress(output.getAddressFirstRep())))
  {
    output.getAddress().clear();
    output.addAddress().setText(addressText);
  }
  if (output.getAddressFirstRep().isEmpty())
  {
    output.setAddress(null);
  }
}
true' WHERE id = 'f0a48e63-cc1d-4d02-85fa-80c7e79a5d9e';

UPDATE fhir_script_source SET source_text=
'if (output.hasAddress() && (output.getAddress().size() < 2))
{
  geoUtils.updateAddress(output.getAddressFirstRep(), input.getCoordinates());
}
true' WHERE id = '650e2d25-82d7-450a-bac0-3a20f31817b2';

ALTER TABLE fhir_rule ADD COLUMN fhir_delete_enabled BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_rule.fhir_delete_enabled IS 'Specifies if the deletion of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';

ALTER TABLE fhir_tracker_program_stage
  ADD COLUMN exp_enabled BOOLEAN DEFAULT FALSE NOT NULL,
  ADD COLUMN fhir_create_enabled BOOLEAN DEFAULT TRUE NOT NULL,
  ADD COLUMN fhir_update_enabled BOOLEAN DEFAULT FALSE NOT NULL,
  ADD COLUMN fhir_delete_enabled BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_tracker_program_stage.exp_enabled IS 'Specifies if transformation from DHIS to FHIR resource is enabled.';
COMMENT ON COLUMN fhir_tracker_program_stage.fhir_create_enabled IS 'Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';
COMMENT ON COLUMN fhir_tracker_program_stage.fhir_update_enabled IS 'Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';
COMMENT ON COLUMN fhir_tracker_program_stage.fhir_delete_enabled IS 'Specifies if the deletion of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';

ALTER TABLE fhir_tracker_program
  ADD COLUMN exp_enabled BOOLEAN DEFAULT FALSE NOT NULL,
  ADD COLUMN fhir_create_enabled BOOLEAN DEFAULT TRUE NOT NULL,
  ADD COLUMN fhir_update_enabled BOOLEAN DEFAULT FALSE NOT NULL,
  ADD COLUMN fhir_delete_enabled BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_tracker_program.exp_enabled IS 'Specifies if transformation from DHIS to FHIR resource is enabled.';
COMMENT ON COLUMN fhir_tracker_program.fhir_create_enabled IS 'Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';
COMMENT ON COLUMN fhir_tracker_program.fhir_update_enabled IS 'Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';
COMMENT ON COLUMN fhir_tracker_program_stage.fhir_delete_enabled IS 'Specifies if the deletion of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';

CREATE TABLE fhir_rule_dhis_data_ref (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  rule_id         UUID                           NOT NULL,
  data_ref        VARCHAR(230)                   NOT NULL,
  script_arg_name VARCHAR(30),
  required        BOOLEAN                        NOT NULL DEFAULT TRUE,
  description     TEXT,
  CONSTRAINT fhir_rule_dhis_data_ref_pk PRIMARY KEY (id),
  CONSTRAINT fhir_rule_dhis_data_ref_uk1 UNIQUE (rule_id, data_ref),
  CONSTRAINT fhir_rule_dhis_data_ref_fk1 FOREIGN KEY (rule_id) REFERENCES fhir_rule(id) ON DELETE CASCADE
);
CREATE INDEX fhir_rule_dhis_data_i1 ON fhir_rule_dhis_data_ref (rule_id);
COMMENT ON TABLE fhir_rule_dhis_data_ref IS 'Contains the references to DHIS2 data elements in which the data that is processed by the assigned rule is stored.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.rule_id IS 'The reference to the rule to which the data references belong to.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.data_ref IS 'The reference to the DHIS2 data element (or depending on the context also to the DHIS2 attribute).';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.script_arg_name IS 'The name of the script argument to which the data reference (as reference argument type) should be passed.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.required IS 'Specifies if a value for the data element is required in order to perform the transformation. It is also used to remove existing FHIR resources when values are reset.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.description IS 'The detailed description of the purpose of the reference to the data element.';

ALTER TABLE fhir_resource_mapping RENAME COLUMN tei_lookup_script_id TO imp_tei_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN enrollment_org_lookup_script_id TO imp_enrollment_org_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN event_org_lookup_script_id TO imp_event_org_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN enrollment_date_lookup_script_id TO imp_enrollment_date_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN event_date_lookup_script_id TO imp_event_date_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN enrollment_loc_lookup_script_id TO imp_enrollment_geo_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN event_loc_lookup_script_id TO imp_event_geo_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN effective_date_lookup_script_id TO imp_effective_date_lookup_script_id;

ALTER TABLE fhir_tracker_program
  ADD COLUMN tracked_entity_fhir_resource_type VARCHAR(30) DEFAULT 'PATIENT' NOT NULL,
  ADD CONSTRAINT fhir_tracker_program_fk6 FOREIGN KEY(tracked_entity_fhir_resource_type) REFERENCES fhir_resource_type_enum(value);

ALTER TABLE fhir_resource_mapping
  ADD COLUMN exp_ou_transform_script_id UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk9 FOREIGN KEY (exp_ou_transform_script_id) REFERENCES fhir_executable_script(id),
  ADD COLUMN exp_geo_transform_script_id UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk10 FOREIGN KEY (exp_geo_transform_script_id) REFERENCES fhir_executable_script(id),
  ADD COLUMN exp_date_transform_script_id UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk11 FOREIGN KEY (exp_date_transform_script_id) REFERENCES fhir_executable_script(id),
  ADD COLUMN exp_absent_transform_script_id UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk12 FOREIGN KEY (exp_absent_transform_script_id) REFERENCES fhir_executable_script(id),
  ADD COLUMN exp_status_transform_script_id UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk13 FOREIGN KEY (exp_status_transform_script_id) REFERENCES fhir_executable_script(id),
  ADD COLUMN exp_delete_when_absent BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_resource_mapping.exp_ou_transform_script_id IS 'References the transformation script that integrates the DHIS organization unit into the FHIR resource.';
COMMENT ON COLUMN fhir_resource_mapping.exp_geo_transform_script_id IS 'References the transformation script that integrates the DHIS GEO location coordinates into the FHIR resource.';
COMMENT ON COLUMN fhir_resource_mapping.exp_date_transform_script_id IS 'References the transformation script that integrates the DHIS effective date into the FHIR resource.';
COMMENT ON COLUMN fhir_resource_mapping.exp_absent_transform_script_id IS 'References the transformation script that integrates the DHIS absence of a required data element into the FHIR resource.';
COMMENT ON COLUMN fhir_resource_mapping.exp_status_transform_script_id IS 'References the transformation script that integrates the DHIS resource status into the FHIR resource.';
CREATE INDEX fhir_resource_mapping_i10 ON fhir_resource_mapping(exp_ou_transform_script_id);
CREATE INDEX fhir_resource_mapping_i11 ON fhir_resource_mapping(exp_geo_transform_script_id);
CREATE INDEX fhir_resource_mapping_i12 ON fhir_resource_mapping(exp_date_transform_script_id);
CREATE INDEX fhir_resource_mapping_i13 ON fhir_resource_mapping(exp_absent_transform_script_id);
CREATE INDEX fhir_resource_mapping_i14 ON fhir_resource_mapping(exp_status_transform_script_id);

-- Script that sets for a data element if a FHIR Immunization has been given
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('c8a937b5-665b-485c-bbc9-7a83e21a4e47', 0, 'TRANSFORM_DHIS_IMMUNIZATION_YN', 'Transforms DHIS Immunization Y/N data element to FHIR', 'Transforms DHIS Immunization Y/N data element to FHIR.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_IMMUNIZATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('c8a937b5-665b-485c-bbc9-7a83e21a4e47', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('c8a937b5-665b-485c-bbc9-7a83e21a4e47', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('c8a937b5-665b-485c-bbc9-7a83e21a4e47', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('36c2d0a9-d923-4476-8ab5-b94808bd5ade', 0, 'c8a937b5-665b-485c-bbc9-7a83e21a4e47',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element with given vaccine on which Y/N must be set.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('6ede1e58-17c1-49bd-a7e4-36ef9c87521f', 0, 'c8a937b5-665b-485c-bbc9-7a83e21a4e47',
'var result;
var given = input.getBooleanValue(args[''dataElement'']);
if (given)
{
  output.setVaccineCode(codeUtils.getRuleCodeableConcept());
  if (output.getVaccineCode().isEmpty())
  {
    output.getVaccineCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
  }
  output.setPrimarySource(input.isProvidedElsewhere(args[''dataElement'']) == false);
  output.setNotGiven(false);
  result = true;
}
else
{
  output.setNotGiven(true);
  result = !output.getIdElement().isEmpty();
}
result', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('6ede1e58-17c1-49bd-a7e4-36ef9c87521f', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('864e47fe-a186-4340-9b4c-d728150fb45b', 0, 'c8a937b5-665b-485c-bbc9-7a83e21a4e47', 'Transforms DHIS Immunization Y/N data element to FHIR', 'TRANSFORM_DHIS_IMMUNIZATION_YN', 'Transforms DHIS Immunization Y/N data element to FHIR.');

-- Script that sets for a data element if a FHIR Immunization has been given (including dose sequence extracted from option value)
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('33952dc7-bbf9-474d-8eaa-ab866a926da3', 0, 'TRANSFORM_DHIS_IMMUNIZATION_OS', 'Transforms DHIS Immunization option set data element to FHIR', 'Transforms DHIS Immunization option set data element to FHIR.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_IMMUNIZATION', 'f18acd12-bc85-4f79-935d-353904eadc0b');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('33952dc7-bbf9-474d-8eaa-ab866a926da3', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('33952dc7-bbf9-474d-8eaa-ab866a926da3', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('33952dc7-bbf9-474d-8eaa-ab866a926da3', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('78b4af9b-c247-4290-abee-44983938b265', 0, '33952dc7-bbf9-474d-8eaa-ab866a926da3',
'var result;
var doseGiven = input.getIntegerOptionValue(args[''dataElement''], 1, args[''optionValuePattern'']);
if (doseGiven == null)
{
  output.setNotGiven(true);
  result = !output.getIdElement().isEmpty();
}
else
{
  output.setVaccineCode(codeUtils.getRuleCodeableConcept());
  if (output.getVaccineCode().isEmpty())
  {
    output.getVaccineCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
  }
  output.setPrimarySource(input.isProvidedElsewhere(args[''dataElement'']) == false);
  output.setNotGiven(false);
  output.setVaccinationProtocol(null);
  output.addVaccinationProtocol().setDoseSequence(doseGiven);
  result = true;
}
result', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('78b4af9b-c247-4290-abee-44983938b265', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a5b648c0-3d7d-4a19-8bda-3ec37f9d31a2', 0, '33952dc7-bbf9-474d-8eaa-ab866a926da3', 'Transforms DHIS Immunization option set data element to FHIR', 'TRANSFORM_DHIS_IMMUNIZATION_OS',
        'Transforms DHIS Immunization option set data element to FHIR.');

ALTER TABLE fhir_code ADD enabled BOOLEAN DEFAULT TRUE NOT NULL;
COMMENT ON COLUMN fhir_code.enabled IS 'Specifies if the code should be used during evaluations and transformations.';
ALTER TABLE fhir_system_code ADD enabled BOOLEAN DEFAULT TRUE NOT NULL;
COMMENT ON COLUMN fhir_system_code.enabled IS 'Specifies if the code should be used during evaluations and transformations.';
ALTER TABLE fhir_code_set_value ADD preferred_export BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_code_set_value.preferred_export IS 'Specifies if the code should be used as preferred code when exporting data.';

UPDATE fhir_code_set SET name = 'Any ' || SUBSTR(name, 5) WHERE name LIKE 'All %';

ALTER TABLE fhir_system_code ADD display_name VARCHAR(230);
UPDATE fhir_system_code sc SET display_name = (SELECT c.name FROM fhir_code c WHERE c.id = sc.code_id);
UPDATE fhir_system_code SET display_name = SUBSTR(display_name, 4) WHERE display_name LIKE 'RC %';
ALTER TABLE fhir_system_code ALTER COLUMN display_name SET NOT NULL;

--- BCG is marked as preferred BCG vaccine
UPDATE fhir_code_set_value SET preferred_export=TRUE
WHERE code_set_id='7348c790-136f-4b4b-a974-f241fb5dbb55' AND code_id='c00aaec0-b02d-480e-9fd6-58578e224e1d';
--- MMR is marked as preferred measles vaccine
UPDATE fhir_code_set_value SET preferred_export=TRUE
WHERE code_set_id='31c6b008-eb0d-48a3-970d-70725b92bd24' AND code_id='71f5536a-2587-45b9-88ac-9aba362a424a';
-- 17D is marked as preferred yellow fever vaccine
UPDATE fhir_code_set_value SET preferred_export=TRUE
WHERE code_set_id='f3769ff6-e994-4182-8d56-572a23b48312' AND code_id='a22bd4d3-9ada-48ce-a05d-fcdcc199ed22';
-- OPV
UPDATE fhir_code_set_value SET preferred_export=TRUE
WHERE code_set_id='bf62319c-d93c-444d-a47c-b91133b3f99a' AND code_id='f2f15a43-6c57-4d57-a32d-d12b468cef7e';
-- DTP preferred is pure DTaP
UPDATE fhir_code_set_value SET preferred_export=TRUE
WHERE code_set_id='bb66ee91-8e86-422c-bb00-5a90ac95a558' AND code_id='f9462e8c-653b-4c6a-a502-8470a1ab2187';
-- Birth Weight
UPDATE fhir_code_set_value SET preferred_export=TRUE
WHERE code_set_id='d4aa72e9-b57e-4d5c-8856-860ebdf460af' AND code_id='bcdc2c18-30ee-4860-9b79-2f810cbcb0f1';
-- Body Weight
UPDATE fhir_code_set_value SET preferred_export=TRUE
WHERE code_set_id='d37dfecb-ce88-4fa4-9a78-44ffe874c140' AND code_id='a748a35e-00e6-4926-abc6-8cde3bb30ee4';
-- 1 minute Apgar Score
UPDATE fhir_code_set_value SET preferred_export=TRUE
WHERE code_set_id='39457ebd-308c-4a44-9302-6fa47aa57b3b' AND code_id='5662bc32-974e-4d18-bddc-b10f510439e6';

-- script may have been deleted in the meanwhile
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
SELECT '253bcef6-42c7-46b2-807d-9da823d59f24', 0, 'a250e109-a135-42b2-8bdb-1c050c1d384c', 'useLocationIdentifierCode', 'BOOLEAN', TRUE, 'true',
'Specifies if the identifier code itself with the default code prefix for locations should be used as fallback when no code mapping for the location identifier code exists.'
FROM fhir_script WHERE id = 'a250e109-a135-42b2-8bdb-1c050c1d384c';

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('655f55b5-3826-4fe7-b38e-1e29bcad09ec', 0, 'Returns FHIR Location Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER_LOC',
        'Returns the FHIR Location Identifier for the DHIS Org Unit.', 'EVALUATE', 'STRING', NULL, NULL);
UPDATE fhir_script SET base_script_id = (SELECT id FROM fhir_script WHERE id = 'a250e109-a135-42b2-8bdb-1c050c1d384c')
WHERE id='655f55b5-3826-4fe7-b38e-1e29bcad09ec';
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('655f55b5-3826-4fe7-b38e-1e29bcad09ec', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('655f55b5-3826-4fe7-b38e-1e29bcad09ec', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('1a2bf7fc-2908-4148-8a53-5edde593a4e9', 0, '655f55b5-3826-4fe7-b38e-1e29bcad09ec',
'var code = null;
if ((input.getCode() != null) && !input.getCode().isEmpty())
{
  code = codeUtils.getByMappedCode(input.getCode(), ''LOCATION'');
  if ((code == null) && args[''useIdentifierCode''])
  {
    code = codeUtils.getCodeWithoutPrefix(input.getCode(), ''LOCATION'');
  }
}
code', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('1a2bf7fc-2908-4148-8a53-5edde593a4e9', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('5090c485-a225-4c2e-a8f6-95fa74ce1618', 0, '655f55b5-3826-4fe7-b38e-1e29bcad09ec',
        'Returns FHIR Location Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER_LOC',
        'Returns FHIR Location Identifier for the DHIS Org Unit.');
-- Link to base script for transformation of Org Unit Code from FHIR Resource
-- (may have been deleted in the meanwhile)
UPDATE fhir_executable_script SET base_executable_script_id = (SELECT id FROM fhir_executable_script WHERE id = '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf')
WHERE id='5090c485-a225-4c2e-a8f6-95fa74ce1618';

INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_LOCATION');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('b3568beb-5a5d-4059-9b4e-3afa0157adbc', 0, 'Transforms DHIS Org Unit to FHIR Location', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_LOC',
        'Transforms DHIS Organization Unit to FHIR Organization.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_ORGANIZATION_UNIT', 'FHIR_LOCATION', 'f8faecad-964f-402c-8cb4-a8271085528d');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('b3568beb-5a5d-4059-9b4e-3afa0157adbc', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('b3568beb-5a5d-4059-9b4e-3afa0157adbc', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('4bb9745e-43c0-455f-beee-98e95c83df3d', 0, 'b3568beb-5a5d-4059-9b4e-3afa0157adbc',
'output.setManagingOrganization(null);
output.setOperationalStatus(null);
output.setPartOf(null);
output.setPhysicalType(null);
if (input.getParentId() != null)
{
  var parentOrganizationUnit = organizationUnitResolver.getMandatoryById(input.getParentId());
  var parentLocation = organizationUnitResolver.getFhirResource(parentOrganizationUnit);
  if (parentLocation == null)
  {
    context.missingDhisResource(parentOrganizationUnit.getResourceId());
  }
  output.getPartOf().setReferenceElement(parentLocation.getIdElement().toUnqualifiedVersionless());
}
if (input.getLevel() === args[''facilityLevel''])
{
  var organization = organizationUnitResolver.getFhirResource(input, ''ORGANIZATION'');
  if (organization == null)
  {
    context.missingDhisResource(input.getResourceId());
  }
  output.getManagingOrganization().setReferenceElement(organization.getIdElement().toUnqualifiedVersionless());
  output.getPhysicalType().addCoding().setSystem(''http://hl7.org/fhir/location-physical-type'').setCode(''si'').setDisplay(''Site'');
}
if (input.getLevel() < args[''facilityLevel''])
{
  output.getPhysicalType().addCoding().setSystem(''http://hl7.org/fhir/location-physical-type'').setCode(''jdn'').setDisplay(''Jurisdiction'');
}
if (input.getClosedDate() == null)
{
  output.setStatus(locationUtils.getLocationStatus(''active''));
}
else
{
  output.setStatus(locationUtils.getLocationStatus(''inactive''));
}
output.setPosition(geoUtils.createLocationPosition(input.getCoordinates()));
output.setName(input.getName());
output.getAlias().clear();
if ((input.getShortName() != null) && !input.getShortName().equals(input.getName()))
{
  output.addAlias(input.getShortName());
}
if ((input.getDisplayName() != null) && !input.getDisplayName().equals(input.getName()) && !fhirResourceUtils.containsString(output.getAlias(), input.getDisplayName()))
{
  output.addAlias(input.getDisplayName());
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4bb9745e-43c0-455f-beee-98e95c83df3d', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description, base_executable_script_id)
VALUES ('b918b4cd-67fc-4d4d-9b74-91f98730acd7', 0, 'b3568beb-5a5d-4059-9b4e-3afa0157adbc',
        'Transforms DHIS Org Unit to FHIR Location', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_LOC',
        'Transforms DHIS Organization Unit to FHIR Location.', 'a8485045-4d62-4b3a-9cfd-5c36760b8d45');

ALTER TABLE fhir_organization_unit_rule
  ADD COLUMN mo_identifier_lookup_script_id UUID,
  ADD CONSTRAINT fhir_organization_unit_rule_fk3 FOREIGN KEY (mo_identifier_lookup_script_id) REFERENCES fhir_executable_script(id);
CREATE INDEX fhir_organization_unit_rule_i2 ON fhir_organization_unit_rule(mo_identifier_lookup_script_id);

UPDATE fhir_rule SET evaluation_order=2 WHERE id='d0e1472a-05e6-47c9-b36b-ff1f06fec352';
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, imp_enabled, applicable_exp_script_id, transform_exp_script_id)
VALUES ('b9546b02-4adc-4868-a4cd-d5d7789f0df0', 0, 'DHIS Organization Unit to FHIR Location', NULL, TRUE, 1, 'LOCATION', 'ORGANIZATION_UNIT', FALSE, NULL, 'b918b4cd-67fc-4d4d-9b74-91f98730acd7');
INSERT INTO fhir_organization_unit_rule(id, identifier_lookup_script_id, mo_identifier_lookup_script_id) VALUES ('b9546b02-4adc-4868-a4cd-d5d7789f0df0', '5090c485-a225-4c2e-a8f6-95fa74ce1618', '66d12e44-471c-4318-827a-0b397f694b6a');

UPDATE fhir_script_source SET source_text=
'var ok = false;
var code = null;
if (output.location)
{
  if (!output.getLocation().isEmpty() && !args[''overrideExisting''])
  {
    ok = true;
  }
  else if ((organizationUnit == null) || (organizationUnit.getCode() == null) || organizationUnit.getCode().isEmpty())
  {
    output.setLocation(null);
    ok = true;
  }
  else
  {
    code = codeUtils.getByMappedCode(organizationUnit.getCode(), ''LOCATION'');
    if ((code == null) && args[''useLocationIdentifierCode''])
    {
      code = codeUtils.getCodeWithoutPrefix(organizationUnit.getCode(), ''LOCATION'');
    }
  }
  if (code != null)
  {
    var resource = fhirClientUtils.findBySystemIdentifier(''LOCATION'', code);
    if (resource == null)
    {
      context.missingDhisResource(organizationUnit.getResourceId());
    }
    output.setLocation(null);
    if (typeof output.addLocation !== ''undefined'')
    {
      output.addLocation().getLocation().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
    }
    else
    {
      output.getLocation().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
    }
    ok = true;
  }
}
if (output.managingOrganization || output.performer || output.serviceProvider)
{
  if (((output.managingOrganization && !output.getManagingOrganization().isEmpty()) || (output.performer && !output.getPerformer().isEmpty()) || (output.serviceProvider && !output.getServiceProvider().isEmpty())) && !args[''overrideExisting''])
  {
    ok = true;
  }
  else if ((organizationUnit == null) || (organizationUnit.getCode() == null) || organizationUnit.getCode().isEmpty())
  {
    if (output.managingOrganization)
    {
      output.setManagingOrganization(null);
      ok = true;
    }
    else if (output.performer)
    {
      output.setPerformer(null);
      ok = true;
    }
    else if (output.serviceProvider)
    {
      output.setServiceProvider(null);
      ok = true;
    }
  }
  else
  {
    code = codeUtils.getByMappedCode(organizationUnit.getCode(), ''ORGANIZATION'');
    if ((code == null) && args[''useIdentifierCode''])
    {
      code = codeUtils.getCodeWithoutPrefix(organizationUnit.getCode(), ''ORGANIZATION'');
    }
  }
  if (code != null)
  {
    var resource = fhirClientUtils.findBySystemIdentifier(''ORGANIZATION'', code);
    if (resource == null)
    {
      context.missingDhisResource(organizationUnit.getResourceId());
    }
    if (output.managingOrganization)
    {
      output.setManagingOrganization(null);
      output.getManagingOrganization().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      ok = true;
    }
    else if (output.performer)
    {
      output.setPerformer(null);
      output.addPerformer().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      ok = true;
    }
    else if (output.serviceProvider)
    {
      output.setServiceProvider(null);
      output.getServiceProvider().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      ok = true;
    }
  }
}
ok' WHERE id = '78c2b73c-469c-4ab4-8244-07e817b72d4a';

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('ed2e0cde-fc19-4468-8d84-6d31841d55e6', 0, 'Transforms GEO Coordinates to FHIR Resource', 'TRANSFORM_DHIS_GEO_FHIR',
        'Transforms GEO coordinates to FHIR Resource.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('ed2e0cde-fc19-4468-8d84-6d31841d55e6', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('ed2e0cde-fc19-4468-8d84-6d31841d55e6', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('dcf956e8-23cc-4934-8e42-0fbc9a23eeb9', 0, 'ed2e0cde-fc19-4468-8d84-6d31841d55e6',
'if (input.coordinate && output.location && (input.getCoordinate() != null))
{
  var location = fhirResourceUtils.createResource(''Location'');
  location.setPosition(geoUtils.createPosition(input.getCoordinate()));
  if (typeof output.getLocationFirstRep !== ''undefined'')
  {
    location.setPartOf(output.getLocationFirstRep().getLocation());
    output.setLocation(null);
    output.getLocationFirstRep().setLocation(fhirResourceUtils.createReference(location));
  }
  else
  {
    location.setPartOf(output.getLocation());
    output.setLocation(fhirResourceUtils.createReference(location));
  }
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('dcf956e8-23cc-4934-8e42-0fbc9a23eeb9', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('30ee57d1-062f-4847-85b9-f2262a678151', 0, 'ed2e0cde-fc19-4468-8d84-6d31841d55e6',
        'Transforms GEO Coordinates to FHIR Resource', 'TRANSFORM_DHIS_GEO_FHIR',
        'Transforms GEO coordinates to FHIR Resource.');

ALTER TABLE fhir_resource_mapping
  ADD exp_tei_transform_script_id  UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk15 FOREIGN KEY (exp_tei_transform_script_id) REFERENCES fhir_executable_script(id);
COMMENT ON COLUMN fhir_resource_mapping.exp_tei_transform_script_id IS 'References the executable script that transforms the TEI FHIR resource into the exported FHIR resource.';
CREATE INDEX fhir_resource_mapping_i16 ON fhir_resource_mapping(exp_tei_transform_script_id);

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('190ae3fa-471e-458a-be1d-3f173f7d3c75', 0, 'Transforms TEI FHIR Patient to FHIR Resource', 'TRANSFORM_TEI_FHIR_PATIENT',
        'Transforms TEI FHIR Patient to FHIR Resource.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('190ae3fa-471e-458a-be1d-3f173f7d3c75', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('190ae3fa-471e-458a-be1d-3f173f7d3c75', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('190ae3fa-471e-458a-be1d-3f173f7d3c75', 'TEI_FHIR_RESOURCE');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('17931cd6-b2dc-4bb9-a9b0-bec16139ce07', 0, '190ae3fa-471e-458a-be1d-3f173f7d3c75',
'var updated = false;
if (output.patient)
{
  output.setPatient(fhirResourceUtils.createReference(teiFhirResource));
  updated = true;
}
else if (output.subject)
{
  output.setSubject(fhirResourceUtils.createReference(teiFhirResource));
  updated = true;
}
updated', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('17931cd6-b2dc-4bb9-a9b0-bec16139ce07', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('f7863a17-86da-42d2-89fd-7f6c3d214f1b', 0, '190ae3fa-471e-458a-be1d-3f173f7d3c75',
        'Transforms TEI FHIR Patient to FHIR Resource', 'TRANSFORM_TEI_FHIR_PATIENT',
        'Transforms TEI FHIR Patient to FHIR Resource.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('094a3f6b-6f36-4a49-8308-5a05b0acc4ce', 0, 'Transforms DHIS event date to FHIR Resource', 'TRANSFORM_DHIS_EVENT_DATE_FHIR_PATIENT',
        'Transforms DHIS event date to FHIR Resource.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('094a3f6b-6f36-4a49-8308-5a05b0acc4ce', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('094a3f6b-6f36-4a49-8308-5a05b0acc4ce', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('4a0b6fde-c0d6-4ad2-89da-992f4a47a115', 0, '094a3f6b-6f36-4a49-8308-5a05b0acc4ce',
'var updated = false;
if (typeof output.dateElement !== ''undefined'')
{
  output.setDateElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.effective !== ''undefined'')
{
  output.setEffective(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.period !== ''undefined'')
{
  output.setPeriod(null);
  output.getPeriod().setStartElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
updated', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4a0b6fde-c0d6-4ad2-89da-992f4a47a115', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('deb4fd13-d5b2-41df-9f30-0fb73b063c8b', 0, '094a3f6b-6f36-4a49-8308-5a05b0acc4ce',
        'Transforms DHIS event date to FHIR Resource', 'TRANSFORM_DHIS_EVENT_DATE_FHIR_PATIENT',
        'Transforms DHIS event date to FHIR Resource.');

UPDATE fhir_resource_mapping SET
  exp_ou_transform_script_id = '416decee-4604-473a-b650-1a997d731ff0',
  exp_geo_transform_script_id = '30ee57d1-062f-4847-85b9-f2262a678151',
  exp_tei_transform_script_id = 'f7863a17-86da-42d2-89fd-7f6c3d214f1b',
  exp_date_transform_script_id = 'deb4fd13-d5b2-41df-9f30-0fb73b063c8b'
WHERE fhir_resource_type='IMMUNIZATION';

UPDATE fhir_script_source SET source_text=
'var mappedCode = null;
if (input.managingOrganization)
{
  var organizationReference = input.managingOrganization;
  if ( organizationReference != null )
  {
    var hierarchy = organizationUtils.findHierarchy( organizationReference );
    if ( hierarchy != null )
    {
      for ( var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++ )
      {
        var code = identifierUtils.getResourceIdentifier( hierarchy.get( i ), ''ORGANIZATION'' );
        if ( code != null )
        {
          mappedCode = codeUtils.getMappedCode( code, ''ORGANIZATION'' );
          if ( (mappedCode == null) && args[''useIdentifierCode''] )
          {
            mappedCode = organizationUtils.existsWithPrefix( code );
          }
        }
      }
    }
  }
}
else if (input.location)
{
  var locationReference = input.location;
  if ( locationReference != null )
  {
    var hierarchy = locationUtils.findHierarchy( locationReference );
    if ( hierarchy != null )
    {
      for ( var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++ )
      {
        var code = identifierUtils.getResourceIdentifier( hierarchy.get( i ), ''LOCATION'' );
        if ( code != null )
        {
          mappedCode = codeUtils.getMappedCode( code, ''LOCATION'' );
          if ( (mappedCode == null) && args[''useIdentifierCode''] )
          {
            mappedCode = locationUtils.existsWithPrefix( code );
          }
        }
      }
    }
  }
}
if (mappedCode == null)
{
  mappedCode = args[''defaultCode''];
}
var ref = null;
if (mappedCode != null)
{
  ref = context.createReference(mappedCode, ''CODE'');
}
if ((ref == null) && args[''useTei''] && (typeof trackedEntityInstance !== ''undefined''))
{
  ref = context.createReference(trackedEntityInstance.organizationUnitId, ''ID'');
}
ref' WHERE id='7b94feba-bcf6-4635-929a-01311b25d975' AND version=0;
DELETE FROM fhir_script_argument WHERE id='c0175733-83fc-4de2-9cd0-a2ae6b92e991' AND version=0;

-- Script that creates an observation with the the body weight from a data element with a specific weight unit
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('83a84bee-eadc-4c8a-b78f-8c8b9269883d', 0, 'TRANSFORM_BODY_WEIGHT_FHIR_OB', 'Transforms Body Weight FHIR Observation', 'Transforms Body Weight to a FHIR Observation and performs weight unit conversion.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION', 'f1da6937-e2fe-47a4-b0f3-8bbff7818ee1');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('83a84bee-eadc-4c8a-b78f-8c8b9269883d', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('83a84bee-eadc-4c8a-b78f-8c8b9269883d', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('83a84bee-eadc-4c8a-b78f-8c8b9269883d', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('cd274a35-6f1c-495a-94ed-f935572fcac3', 0, '83a84bee-eadc-4c8a-b78f-8c8b9269883d',
'output.setCode(codeUtils.getRuleCodeableConcept());
if (output.getCode().isEmpty())
{
  output.getCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
}
output.setCategory(null);
output.addCategory().addCoding().setSystem(''http://hl7.org/fhir/observation-category'').setCode(''vital-signs'').setDisplay(''Vital Signs'');
var weight = input.getIntegerValue(args[''dataElement'']);
var weightUnit = vitalSignUtils.getWeightUnit(args[''weightUnit'']);
output.addChild(''valueQuantity'').setValue(args[''round''] ? Math.round(weight) : weight).setUnit(weightUnit.getUcumCode()).setSystem(''http://unitsofmeasure.org'');
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('cd274a35-6f1c-495a-94ed-f935572fcac3', 'DSTU3');

-- Script that sets the FHIR immunization to absent
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('475e192c-09f2-4357-b545-c1069b9518b3', 0, 'TRANSFORM_ABSENT_FHIR_IM', 'Transforms absence of data element to FHIR Immunization', 'Transforms absence of data element to FHIR Immunization.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_IMMUNIZATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('475e192c-09f2-4357-b545-c1069b9518b3', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('475e192c-09f2-4357-b545-c1069b9518b3', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('475e192c-09f2-4357-b545-c1069b9518b3', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('235ffb1a-133e-4046-bdd3-498cf0ff771d', 0, '475e192c-09f2-4357-b545-c1069b9518b3',
'output.setNotGiven(true); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('235ffb1a-133e-4046-bdd3-498cf0ff771d', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a5e07a03-69ba-45b5-b58f-247666ec21d0', 0, '475e192c-09f2-4357-b545-c1069b9518b3',
        'Transforms absence of data element to FHIR Immunization', 'TRANSFORM_ABSENT_FHIR_IM',
        'Transforms absence of data element to FHIR Immunization.');

-- Script that checks if FHIR immunization should be deleted
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('f38d27f2-b912-4cbf-9250-b62f5be747ff', 0, 'CHECK_DELETE_FHIR_IM', 'Returns if FHIR immunization should be deleted', 'Returns if FHIR immunization should be deleted based on the input data.',
'EVALUATE', 'BOOLEAN', 'DHIS_EVENT', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f38d27f2-b912-4cbf-9250-b62f5be747ff', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f38d27f2-b912-4cbf-9250-b62f5be747ff', 'INPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('028f5a98-b5ca-4893-8b21-5797fd110f0f', 0, 'f38d27f2-b912-4cbf-9250-b62f5be747ff',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element on which the deletion check is made.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('ab8525cf-1e54-476a-813a-329c23ad5672', 0, 'f38d27f2-b912-4cbf-9250-b62f5be747ff',
'var value = input.getBooleanValue(args[''dataElement'']); ((value == null) || (value == false))', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('ab8525cf-1e54-476a-813a-329c23ad5672', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('2db4dba6-b445-4fce-a9a5-b3f24d0a12cc', 0, 'f38d27f2-b912-4cbf-9250-b62f5be747ff',
        'Returns if FHIR immunization should be deleted', 'CHECK_DELETE_FHIR_IM',
        'Returns if FHIR immunization should be deleted based on the input data.');

ALTER TABLE fhir_program_stage_rule
  ADD exp_delete_evaluate_script_id UUID,
  ADD CONSTRAINT fhir_program_stage_rule_fk5 FOREIGN KEY (exp_delete_evaluate_script_id) REFERENCES fhir_executable_script(id);
COMMENT ON COLUMN fhir_program_stage_rule.exp_delete_evaluate_script_id IS 'References the evaluation script that checks if the FHIR resources should be deleted according to the content of the DHIS data element.';

UPDATE fhir_resource_mapping SET
  exp_absent_transform_script_id='a5e07a03-69ba-45b5-b58f-247666ec21d0'
WHERE fhir_resource_type = 'IMMUNIZATION';

-- Script that sets the FHIR observation to absent
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('ba1f2dad-c008-4ded-8b02-108f94fad74c', 0, 'TRANSFORM_ABSENT_FHIR_OB', 'Transforms absence of data element to FHIR Observation', 'Transforms absence of data element to FHIR Observation.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ba1f2dad-c008-4ded-8b02-108f94fad74c', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ba1f2dad-c008-4ded-8b02-108f94fad74c', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ba1f2dad-c008-4ded-8b02-108f94fad74c', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('efeb5e98-5760-4d12-a0c4-20efb1d71174', 0, 'ba1f2dad-c008-4ded-8b02-108f94fad74c',
'output.setStatus(observationUtils.getObservationStatus(''entered-in-error'')); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('efeb5e98-5760-4d12-a0c4-20efb1d71174', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('9d591156-05de-4e12-8986-dfcaaa1f0f25', 0, 'ba1f2dad-c008-4ded-8b02-108f94fad74c',
        'Transforms absence of data element to FHIR Observation', 'TRANSFORM_ABSENT_FHIR_OB',
        'Transforms absence of data element to FHIR Observation.');

-- Script that sets the FHIR observation to the status of the DHIS event
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('6a644b78-7878-4512-adad-a710a5c901d4', 0, 'TRANSFORM_STATUS_FHIR_OB', 'Transforms DHIS event status to FHIR Observation', 'Transforms DHIS event status to FHIR Observation.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6a644b78-7878-4512-adad-a710a5c901d4', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6a644b78-7878-4512-adad-a710a5c901d4', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6a644b78-7878-4512-adad-a710a5c901d4', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('b3d2ce26-ec5f-480d-abc6-910bedc7d38c', 0, '6a644b78-7878-4512-adad-a710a5c901d4',
'output.setStatus(observationUtils.getObservationStatus(input.getStatus() == ''COMPLETED'' ? ''final'' : ''preliminary'')); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('b3d2ce26-ec5f-480d-abc6-910bedc7d38c', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a78e3852-e18b-44bc-a2c6-9cae29974439', 0, '6a644b78-7878-4512-adad-a710a5c901d4',
        'Transforms DHIS event status to FHIR Observation', 'TRANSFORM_STATUS_FHIR_OB',
        'Transforms DHIS event status to FHIR Observation.');

-- coordinate must be used from encounter
UPDATE fhir_resource_mapping SET
  exp_absent_transform_script_id='9d591156-05de-4e12-8986-dfcaaa1f0f25',
  exp_status_transform_script_id='a78e3852-e18b-44bc-a2c6-9cae29974439',
  exp_ou_transform_script_id = '416decee-4604-473a-b650-1a997d731ff0',
  exp_tei_transform_script_id = 'f7863a17-86da-42d2-89fd-7f6c3d214f1b',
  exp_date_transform_script_id = 'deb4fd13-d5b2-41df-9f30-0fb73b063c8b'
WHERE fhir_resource_type = 'OBSERVATION';

-- Script that sets for a data element the Apgar Score
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('30760578-0c97-43be-ad28-324ccbbad249', 0, 'TRANSFORM_APGAR_SCORE_FHIR_OB', 'Transforms to FHIR Observation Apgar Score', 'Transforms data element to FHIR Observation Apgar Score.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION', 'd69681b8-2e37-42d7-b229-9ed21ce76cf3');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('2fc991ef-4ece-4a5a-a095-b7c976b6ef00', 0, '30760578-0c97-43be-ad28-324ccbbad249',
'output.setCode(codeUtils.getRuleCodeableConcept());
if (output.getCode().isEmpty())
{
  output.getCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
}
output.setCategory(null);
output.addCategory().addCoding().setSystem(''http://hl7.org/fhir/observation-category'').setCode(''survey'').setDisplay(''Survey'');
var score = input.getIntegerValue(args[''dataElement'']);
output.addChild(''valueQuantity'').setValue(score).setCode(''{score}'');
var comment = input.getStringValue(args[''commentDataElement'']);
output.setComment(((comment != null) && (comment.length() > 0)) ? comment : null);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('2fc991ef-4ece-4a5a-a095-b7c976b6ef00', 'DSTU3');

UPDATE fhir_script_source SET source_text=
'var updated = false; var latestApgarScore = observationUtils.queryLatestPrioritizedByMappedCodes(''subject'', ''Patient'', input.getSubject().getReferenceElement(), args[''mappedApgarScoreCodes''], null);
if ((latestApgarScore != null) && latestApgarScore.hasValueQuantity())
{
  var apgarScore = latestApgarScore.getValueQuantity().getValue();
  updated = output.setValue(args[''dataElement''], apgarScore, null, context.getFhirRequest().getLastUpdated());
  if (updated && (args[''commentDataElement''] != null))
  {
    if ((args[''maxCommentApgarScore''] == null) || (apgarScore <= args[''maxCommentApgarScore'']))
    {
      var text = observationUtils.getComponentText(latestApgarScore);
      output.setValue(args[''commentDataElement''], (text == null) ? input.getComment() : text);
    }
  }
}
updated' WHERE id = '628348ae-9a40-4f0d-af78-5c6e0bfa1c6d';

CREATE TABLE fhir_dhis_assignment(
  id                      UUID NOT NULL,
  created_at              TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  remote_subscription_id  UUID NOT NULL,
  rule_id                 UUID NOT NULL,
  fhir_resource_id        VARCHAR(200) NOT NULL,
  dhis_resource_id        VARCHAR(200) NOT NULL,
  CONSTRAINT fhir_dhis_assignment_pk PRIMARY KEY (id),
  CONSTRAINT fhir_dhis_assignment_fk1 FOREIGN KEY (remote_subscription_id) REFERENCES fhir_remote_subscription(id) ON DELETE CASCADE ,
  CONSTRAINT fhir_dhis_assignment_fk2 FOREIGN KEY (rule_id) REFERENCES fhir_rule(id) ON DELETE CASCADE ,
  CONSTRAINT fhir_dhis_assignment_uk1 UNIQUE (rule_id, remote_subscription_id, fhir_resource_id),
  CONSTRAINT fhir_dhis_assignment_uk2 UNIQUE (rule_id, remote_subscription_id, dhis_resource_id)
);
CREATE INDEX fhir_dhis_assignment_i1 ON fhir_dhis_assignment(remote_subscription_id);
COMMENT ON TABLE fhir_dhis_assignment IS 'Contains the assignments from DHIS to FHIR resource IDs and vice versa.';
COMMENT ON COLUMN fhir_dhis_assignment.id IS 'The unique ID of the assignment.';
COMMENT ON COLUMN fhir_dhis_assignment.created_at IS 'The timestamp when the assignment has been created.';
COMMENT ON COLUMN fhir_dhis_assignment.remote_subscription_id IS 'The reference to the remote subscription to which the resource belongs to.';
COMMENT ON COLUMN fhir_dhis_assignment.rule_id IS 'The rule that created the assignment.';
COMMENT ON COLUMN fhir_dhis_assignment.fhir_resource_id IS 'The unqualified FHIR resource ID part.';
COMMENT ON COLUMN fhir_dhis_assignment.dhis_resource_id IS 'The unqualified DHIS resource ID part.';

INSERT INTO fhir_transform_data_type_enum VALUES ('FHIR_ENCOUNTER');
-- Script that sets the FHIR encounter to absent
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('37323ef8-5dd2-4d86-b82d-a9c43b0df27a', 0, 'TRANSFORM_ABSENT_FHIR_EN', 'Transforms absence of data element to FHIR Encounter', 'Transforms absence of data element to FHIR Encounter.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_ENCOUNTER');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('37323ef8-5dd2-4d86-b82d-a9c43b0df27a', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('37323ef8-5dd2-4d86-b82d-a9c43b0df27a', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('37323ef8-5dd2-4d86-b82d-a9c43b0df27a', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('6dd56290-b513-4c9a-ba4b-6c5169b16559', 0, '37323ef8-5dd2-4d86-b82d-a9c43b0df27a',
'output.setStatus(encounterUtils.getEncounterStatus(''cancelled'')); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('6dd56290-b513-4c9a-ba4b-6c5169b16559', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('c51681da-3d4c-4936-bec9-91f53cc05953', 0, '37323ef8-5dd2-4d86-b82d-a9c43b0df27a',
        'Transforms absence of data element to FHIR Encounter', 'TRANSFORM_ABSENT_FHIR_EN',
        'Transforms absence of data element to FHIR Encounter.');

-- Script that sets the FHIR encounter to the status of the DHIS event
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('ee82d0ca-a5d6-417f-9dae-dbe84ccf8964', 0, 'TRANSFORM_STATUS_FHIR_EN', 'Transforms DHIS event status to FHIR Encounter', 'Transforms DHIS event status to FHIR Encounter.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_ENCOUNTER');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ee82d0ca-a5d6-417f-9dae-dbe84ccf8964', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ee82d0ca-a5d6-417f-9dae-dbe84ccf8964', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ee82d0ca-a5d6-417f-9dae-dbe84ccf8964', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('78490c08-217f-40ba-8663-d51dbe730b6d', 0, 'ee82d0ca-a5d6-417f-9dae-dbe84ccf8964',
'var eventStatus = input.getStatus();
var encounterStatus = ''planned'';
if (eventStatus == ''COMPLETED'')
{
  encounterStatus = ''finished'';
}
else if (eventStatus == ''SKIPPED'')
{
  encounterStatus = ''cancelled'';
}
else if (eventStatus == ''ACTIVE'')
{
  encounterStatus = ''in-progress'';
}
output.setStatus(encounterUtils.getEncounterStatus(encounterStatus));
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('78490c08-217f-40ba-8663-d51dbe730b6d', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a726bc90-8152-48a3-9427-55477a948907', 0, 'ee82d0ca-a5d6-417f-9dae-dbe84ccf8964',
        'Transforms DHIS event status to FHIR Encounter', 'TRANSFORM_STATUS_FHIR_EN',
        'Transforms DHIS event status to FHIR Encounter.');

INSERT INTO fhir_resource_type_enum VALUES ('ENCOUNTER');
INSERT INTO fhir_resource_mapping(id, version, fhir_resource_type,
                                  exp_absent_transform_script_id, exp_status_transform_script_id,
                                  exp_tei_transform_script_id, exp_ou_transform_script_id, exp_geo_transform_script_id,
                                  exp_date_transform_script_id)
VALUES ('248d964c-4dbf-4271-bb57-22f5aed5c9f9', 0, 'ENCOUNTER',
        'c51681da-3d4c-4936-bec9-91f53cc05953', 'a726bc90-8152-48a3-9427-55477a948907',
        'f7863a17-86da-42d2-89fd-7f6c3d214f1b', '416decee-4604-473a-b650-1a997d731ff0', '30ee57d1-062f-4847-85b9-f2262a678151',
        'deb4fd13-d5b2-41df-9f30-0fb73b063c8b');

-- Script that sets the FHIR observation to the status of the DHIS event
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('1e06ef05-2466-4ed9-b32e-7f9dfecf4d45', 0, 'TRANSFORM_EVENT_FHIR_EN', 'Transforms DHIS event to FHIR Encounter', 'Transforms DHIS event to FHIR Encounter.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_ENCOUNTER');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('1e06ef05-2466-4ed9-b32e-7f9dfecf4d45', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('1e06ef05-2466-4ed9-b32e-7f9dfecf4d45', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('1e06ef05-2466-4ed9-b32e-7f9dfecf4d45', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('cfec6b38-4777-49fd-944d-ddab80421bc2', 0, '1e06ef05-2466-4ed9-b32e-7f9dfecf4d45',
'output.setType(null);
output.addType().setText(program.getName() + '': '' + programStage.getName());
context.setAttribute(''group-encounter-event-'' + input.getId(), output);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('cfec6b38-4777-49fd-944d-ddab80421bc2', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('5eab76a9-0ff4-43b0-a7d0-5a6e726ca80e', 0, '1e06ef05-2466-4ed9-b32e-7f9dfecf4d45',
        'Transforms DHIS event to FHIR Encounter', 'TRANSFORM_EVENT_FHIR_EN',
        'Transforms DHIS event to FHIR Encounter.');

-- Script that sets the collected FHIR encounter to the updated FHIR resource
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('45c39ea9-3f44-4db9-8ce5-34d23031db27', 0, 'TRANSFORM_GROUP_FHIR', 'Transforms the grouping FHIR resource to FHIR resource', 'Transforms the grouping FHIR resource to FHIR resource.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('45c39ea9-3f44-4db9-8ce5-34d23031db27', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('45c39ea9-3f44-4db9-8ce5-34d23031db27', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('45c39ea9-3f44-4db9-8ce5-34d23031db27', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('9c4c5b88-52b8-4bae-b07f-7f3ee7392d13', 0, '45c39ea9-3f44-4db9-8ce5-34d23031db27',
'var encounter = context.getAttribute(''group-encounter-event-'' + input.getId());
var updated = false;
if (output.encounter)
{
  output.setEncounter(null);
  if (encounter != null)
  {
    output.setEncounter(fhirResourceUtils.createReference(encounter));
  }
  updated = true;
}
else if (output.context)
{
  output.setContext(null);
  if (encounter != null)
  {
    output.setContext(fhirResourceUtils.createReference(encounter));
  }
  updated = true;
}
updated', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('9c4c5b88-52b8-4bae-b07f-7f3ee7392d13', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('2347ba1f-2d5b-4276-8934-100cbdb0fcfa', 0, '45c39ea9-3f44-4db9-8ce5-34d23031db27',
        'Transforms the grouping FHIR resource to FHIR resource', 'TRANSFORM_GROUP_FHIR',
        'Transforms the grouping FHIR resource to FHIR resource.');

ALTER TABLE fhir_resource_mapping
  ADD COLUMN exp_group_transform_script_id UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk16 FOREIGN KEY (exp_group_transform_script_id) REFERENCES fhir_executable_script(id);
COMMENT ON COLUMN fhir_resource_mapping.exp_group_transform_script_id IS 'Specifies the transformation script that sets the group reference (e.g. encounter).';
CREATE INDEX fhir_resource_mapping_i17 ON fhir_resource_mapping(exp_group_transform_script_id);
UPDATE fhir_resource_mapping SET exp_group_transform_script_id = '2347ba1f-2d5b-4276-8934-100cbdb0fcfa' WHERE fhir_resource_type='IMMUNIZATION';
UPDATE fhir_resource_mapping SET exp_group_transform_script_id = '2347ba1f-2d5b-4276-8934-100cbdb0fcfa' WHERE fhir_resource_type='OBSERVATION';
