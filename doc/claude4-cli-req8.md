# Requirement 8: Dynamic Date/Time Proposal Addition on Step 2

**Context**: This requirement enhances the existing Schedule Event Step 2 page (`/schedule-event-step2/{uuid}`) to allow users to dynamically add multiple date/time proposals for their event beyond the current fixed set of inputs.

## User Story
As a user creating an event poll on Step 2, I want to be able to add multiple date/time proposals dynamically so that I can offer participants more scheduling options without being limited to a fixed number of time slots.

## TDD Implementation Approach
**CRITICAL**: Follow strict Test-Driven Development - write failing tests FIRST, then implement just enough code to make them pass.

### Test-First Development Steps:

1. **FIRST**: Write failing test for "+" button presence on step 2 page
2. **THEN**: Add "+" button to step 2 template
3. **FIRST**: Write failing test for adding second date/time proposal set
4. **THEN**: Implement dynamic form field addition with JavaScript/server-side logic
5. **FIRST**: Write failing test for preserving existing data when adding new fields
6. **THEN**: Implement data preservation during form expansion
7. **FIRST**: Write failing test for ignoring empty proposals during save
8. **THEN**: Implement server-side filtering of empty proposals
9. **FIRST**: Write failing test for duplicate prevention
10. **THEN**: Implement validation to prevent duplicate date/time entries

## Acceptance Criteria

### 1. Add Button Display (Test First!)
- Test that step 2 page displays a "+" button with plus symbol image only (no text)
- Button should have `data-test="add-proposal-button"` attribute
- Button should be clearly visible and positioned appropriately on the form
- Use existing plus symbol image: `doc/Plus-Symbol-Transparent-small.png`
- Button should contain only the plus symbol image, no additional text

### 2. Dynamic Field Addition (Test First!)
- Test that clicking "+" button adds a new set of date/time input fields
- New fields should include:
  - Date input field (`eventDate2`, `eventDate3`, etc.)
  - Time slot inputs (`timeSlot2_1`, `timeSlot2_2`, `timeSlot2_3` for second proposal)
- Test that multiple "+" clicks continue adding proposal sets incrementally
- Test that there's no artificial limit on number of proposals

### 3. Data Preservation (Test First!)
- Test that existing entered data is preserved when adding new proposal sets
- Test that form submission includes all entered proposals (non-empty ones)
- Test that page reload/refresh maintains all entered data via UUID-based storage

### 4. Empty Proposal Handling (Test First!)
- Test that completely empty date/time proposals are ignored during save
- Test that partially filled proposals (date but no times, or times but no date) are handled appropriately
- Test that `PollStorageService.updatePollData()` only receives non-empty proposals

### 5. Duplicate Prevention (Test First!)
- Test that duplicate date/time combinations are prevented or flagged
- Test validation feedback when user tries to enter identical proposals

### 6. Navigation Continuity (Test First!)
- Test that "Next" button continues to work with multiple proposals
- Test that "Back" button preserves all entered proposals
- Test that step 3 receives and can access all valid proposals

## Technical Implementation Hints

### Controller Updates
- Extend `submitScheduleEventStep2()` in `WoodleFormsController` to handle dynamic number of proposals
- Use dynamic parameter names like `eventDate1`, `eventDate2`, `timeSlot1_1`, `timeSlot1_2`, etc.
- Implement server-side validation and filtering of empty/invalid proposals

### Template Updates
- Modify `schedule-event-step2.jte` to support dynamic form fields
- Add JavaScript for client-side form field addition (optional, can be server-side only)
- Ensure proper CSS styling for dynamically added fields

### Storage Schema
- Update `PollStorageService` storage format to handle multiple date/time proposals
- Consider JSON structure: `{"proposals": [{"date": "2024-01-15", "times": ["10:00", "14:00"]}, ...]}`

### Testing Strategy
- Use `@WebMvcTest(WoodleFormsController.class)` with `@ImportAutoConfiguration(JteAutoConfiguration.class)`
- Use `@MockitoBean` for PollStorageService isolation
- Use JGiven BDD format for readable test scenarios
- Test with existing UUID patterns from previous requirements
- Use Jsoup for HTML parsing and dynamic content verification

## Integration Points
- Extend existing `WoodleFormsController.submitScheduleEventStep2()` method
- Build on existing UUID-based data storage from req3-req6
- Integrate with existing JTE template structure from req1-req2
- Ensure compatibility with existing step 3 implementation

## Example Test Scenarios

```java
@Test
void shouldDisplayAddProposalButtonOnStep2() {
    // Test that "+" button is present
}

@Test
void shouldAddSecondProposalSetWhenPlusButtonClicked() {
    // Test dynamic field addition
}

@Test
void shouldPreserveExistingDataWhenAddingNewProposal() {
    // Test data preservation
}

@Test
void shouldIgnoreEmptyProposalsWhenSaving() {
    // Test empty proposal filtering
}

@Test
void shouldPreventDuplicateProposals() {
    // Test duplicate validation
}
```