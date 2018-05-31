VAR met_before = true
VAR c_name = "Ralph Macchio"
VAR guessed_right = false

VAR name_guess_0 = "Ralph Macchio"
VAR name_guess_1 = "Elijah Woods"
VAR name_guess_2 = "Ivan The Terrible"

-> first_meeting

=== first_meeting
Hello! 

{ not met_before :
{~Anyways, nice to meet you | We haven't met before, I believe? | A friendly face, a pleasant surprise! } 
- else: {~I recognize you! | We met before, I think? | We meet again, I believe? }
}

Everyone here has forgotten their name. 
Me too.
It's quite alarming.

{ met_before: 
 {~I still can't remember my name - do you? | You wouldn't know my name, would you? }
 - else: {~You wouldn't happen to know my name? | Would you wager a guess on my name?}
}

* [Sure! Your name is {name_guess_0}, isn't it?] -> name_guess (name_guess_0)
* [Surely you must be {name_guess_1}?] -> name_guess (name_guess_1)
* [{name_guess_2} I presume?] -> name_guess (name_guess_2)

=== correct_name_guess
{~Why yes, yes, that's it|Aah, how could you guess right so quickly?|What a relief, that's it!}! -> END

=== wrong_guess
{~No, no, that's not it, I'm sure|Rings a bell, but not quite the right ring to it|A nice name indeed, but not mine, I fear} -> first_meeting


=== name_guess(guessed_name)
{
    - guessed_name == c_name : -> correct_name_guess
    - else : -> wrong_guess
}

->END