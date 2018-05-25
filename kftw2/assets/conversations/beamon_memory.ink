VAR met_before = false
VAR c_name = "Petter Knöös"
VAR knows_c_name = false
VAR guessed_right = false

VAR name_guess_0 = "Petter Knöös"
VAR name_guess_1 = "Frank Artschwager"
VAR name_guess_2 = "Ellika Skoogh"

{ not met_before } -> first_meeting

=== first_meeting
Hej! {~Trevligt att råkas | Vi har inte setts förut, tror jag? | Ah, en kollega, hur står det till? }
* [Hejsan du heter {name_guess_0} va?] -> name_guess (name_guess_0)
* [Visst är det du som är {name_guess_1}?] -> name_guess (name_guess_1)
* [{name_guess_2} I presume?] -> name_guess (name_guess_2)

=== correct_name_guess
Ah, du visste mitt namn! -> END

=== wrong_guess
Nej, då minns du nog fel! -> first_meeting


=== name_guess(guessed_name)
{
    - guessed_name == c_name : -> correct_name_guess
    - else : -> wrong_guess
}

->END