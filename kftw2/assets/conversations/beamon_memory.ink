VAR met_before = false
VAR c_name = "Tommie Nygren"
VAR knows_c_name = false
VAR guessed_right = false

VAR name_guess_1 = "Petter Knöös"
VAR name_guess_2 = "Frank Artschwager"
VAR name_guess_3 = "Ellika Skoogh"

{ not met_before } -> first_meeting

=== first_meeting
Hej! {~Trevligt att råkas | Vi har inte setts förut, tror jag? | Ah, en kollega, hur står det till? }
-> correct_name_guess


=== correct_name_guess
Ah, du visste mitt namn! -> END


->END