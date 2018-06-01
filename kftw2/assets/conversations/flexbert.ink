VAR met_before = false
VAR player_name = "Hulk Hogan"

-> first_meeting
=== first_meeting
... Hej? 
{ not met_before :
{~Nice to meet you.|We haven't met before, I believe?|A friendly face, a pleasant surprise! }
- else:{~I recognize you! |We met before, I think? |We meet again, I believe? }
}
{~There's something about this place...|I just have such a hard time remembering things...}

{ met_before:
 {~I still can't remember my name - do you? |You wouldn't happen know my name, would you? }
 - else: {~Do you know my name? |Would you wager a guess on my name?}
} -> talk_shit

=== talk_shit


#=== guess_some_names
#
#* [I haven't got the slightest idea, I'm afraid...] -> no_guess
#
#=== correct_name_guess
#~guessed_right = true
#{~Why yes, yes, that's it|Aah, how could you guess right so quickly?|What a relief, that's it!}!
#-> END

#=== wrong_guess
#{~No, no, that's not it, I'm sure.|Rings a bell, but not quite the right ring to it.|A nice name indeed, but not mine, I #fear.}
#Care to guess again?
#-> guess_some_names
#=== name_guess(guessed_name)
#{
#    - guessed_name == c_name : -> correct_name_guess
#    - else : -> wrong_guess
#}
#=== no_guess
#{~That's alright, it'll be OK. |Not to worry, it'll come back to me. |Go on, wander on, I'll make it out of here.}
->END