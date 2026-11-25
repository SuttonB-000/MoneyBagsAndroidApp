color pallets:
    ["#022b3a","#048ba8","#99c24d","#c7e293","#e2e8da"]  <-- "ocean misty green"

    ["#05668d","#427aa1","#ebf2fa","#679436","#a5be00"] <-- "sky blue freshness"

    ["476eae","48b3af","a7e399","f6ff99"]

# Money Bags Design Notes

## The Purpose
  Most budgeting apps I have found, require connections to bank accounts and provide limited 1 dimmension budgeting.
It takes the total I have and allows me to divide up what I want but lacks the ability to carry the balance over to
the next paycheck. Most budgeting apps I have experienced are glorified spreadsheets, however money bags is built to 
be a visualization and supports a persistent balance for each budget item. It will allow spending from specific budgets
called "money bags". This allows users to take the lump sum in thier bank accounts and provide visual representation 
of each budget

## Activities and Navigation

| Activity | Role | Status |
| --- | --- | --- |
| main | display bags and handle balance changes | implemented |
| settings | Change theeme, text size, and currency | implemented |
| secondary (Money Allocation) | Receive the unallocated balance from Main and let the user allocate funds to a bag | planned |
| Help | Explain the app and its preferences | Dialogue implemented, needs revised, planned |

Main opens the Money Allocation Activity with an intent containing the current unallocated balance. The secondary activity displays that amount and uses it to prevent the user from allocating more money than is available. The intent carries a snapshot of the balance when the activity opens; it does not save the balance. When the user confirms a transfer, the updated bag and unallocated balances must be saved and reflected on Main.


### Secondary Activity -- Money Allocation
A user will add funds to a total pool, this would represent a users bank account balance but it is not directly connected to thier bank account. From there they 
can then add money from the total to individual bags. The unallocated money is any money not already in a bag:
**Overall Balance = all bag balances + unallocated funds**
the activity would work like:
- Main shows the overall balance with the unallocated balance directly beneath it
- Tapping allocate opens the secondary activity 
- the secondary activity shows the available unallocated amount and lets the user assign  some of it to one bag
- as amounts are entered, remaining un allocated amount updates confirming moves the money into those bags.
- deleting a money bag will return it's balance to the unallocated amount
- any new money bags created will start with a balance of 0.00, the user can then allot funds to it from the unallocated amount as a separate action
- decreasing amounts in a money bag can be accomplished from the editing feature. If a bags balance drops, the difference from the previous amount 
  and the new amount returns to unallocated


**What -$ does in this workflow**
- -$ will still operate as a spending tracking action. When a user selects -$ they will be asked to select a bag and enter
  an amount. The amount reduces the value of the bag, and reduces the overall balance

**Allocation flow**
tap "allocate" --> tap bag image to allocate --> dialogue asking what amount should be added --> user enters amount --> user input is checked against guardrails --> confirmation dialogue pops up --> "confirm transfer to $bagName?" --> yes --> balance is updated for both the bag, and unallocated amounts
- guardrails for this operation:
  - a user cannot allocate more than the "unallocated balance"
  - user input can only be numeric
  

**fund reduction -- not spending**
long press on a money bag --> edit screen appears --> user edits balance to less than it was --> difference returns to the unallocated amount
- guardrails for this operation:
  - a user cannot enter values less than 0
  - user input can only be numeric

**Money Bag Editing** 
- a user may increase amounts from the edit screen but the increase amount exceed the unallocated amount

**issues**
--issue? **+$** already handles this => change it so that **+$** adds money to the unallocated amount which will influence the presented overall balance 
    this is a better and more logical flow. Money comes in from a paycheck and is recorded, then the user can choose which budget item gets allocated


## Data handling 
Bag names and balances currently live in Main's compose screen state. They are not saved across a fresh app launch.
Settings are saced with sharedpreferences through appPreferences

## Planned Data Layer
A repository would provide one place for Main and Secondary to read and update money bags. The screens would ask
the repo for the bag data, then the repository would handle whichever persistant storage chosen

main and secondary --> Bag repository --> persistent storage

unallocated amounts should also survive closing and repopening the app

this is a planned design, not an implemented feature. Decide storage metho before describing it as part of the app

## Design Decisions 
### Money Bags
The money bags represent budget line items or categories such as Grocereies, Fuel, or Saving for a vacation. The money bags represent 
those categories 
### Long Press Controls
Instead of using more buttons to access bag controls. A long press was implemented. This decision allows for better visibility of the 
main screen, and reduced visual clutter. A long press in an empty spot allows the user to create a new money bag (a budget category),
while a long press on an already existing money bag opens a menu that allows a user to edit that bag (that budget category)
### Color Schemes, themes, and typography
The current color scheme uses green and blue, and some shades between those. I believe green is neccessary because of its correalation
with dollar bills, while the blue is a calming color. The currect color scheme is not set in stone, and may change in the 
future. 
### Currency Symbol Changes but alreay existing values do not convert
When changing the currency symbols the values do not convert. This is solely to reduce complexity. The exchange can vary and would
require to outside access. This is personal finance app, but not a currency converter. It is assumed users of this
app will be tracking one currency.
